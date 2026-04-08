package com.smartbilling.smartbilling.auth.service;


import com.smartbilling.smartbilling.auth.domain.Role;
import com.smartbilling.smartbilling.auth.domain.Token;
import com.smartbilling.smartbilling.auth.domain.TokenType;
import com.smartbilling.smartbilling.auth.domain.User;
import com.smartbilling.smartbilling.auth.dto.requests.ForgotPasswordRequest;
import com.smartbilling.smartbilling.auth.dto.requests.ResetPasswordRequest;
import com.smartbilling.smartbilling.auth.dto.responses.MessageResponse;
import com.smartbilling.smartbilling.auth.repository.UserRepository;
import com.smartbilling.smartbilling.auth.service.EmailService;
import com.smartbilling.smartbilling.auth.service.TokenService;
import com.smartbilling.smartbilling.auth.service.serviceImpl.PasswordServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PasswordServiceImpl — Tests unitaires")
class PasswordServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private TokenService tokenService;
    @Mock private EmailService emailService;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordServiceImpl passwordService;

    private User user;
    private Token resetLinkToken;
    private Token resetOtpToken;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("john@example.com");
        user.setPassword("encodedOldPassword");
        user.setRole(Role.User);

        resetLinkToken = new Token();
        resetLinkToken.setValue("reset-link-uuid");
        resetLinkToken.setType(TokenType.PASSWORD_RESET_LINK);
        resetLinkToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        resetLinkToken.setUser(user);

        resetOtpToken = new Token();
        resetOtpToken.setValue("847291");
        resetOtpToken.setType(TokenType.PASSWORD_RESET_OTP);
        resetOtpToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        resetOtpToken.setUser(user);
    }

    // ══════════════════════════════════════════════════════════
    //  FORGOT PASSWORD
    // ══════════════════════════════════════════════════════════

    @Test
    @DisplayName("forgotPassword — succès : envoie lien + OTP")
    void forgotPassword_success() {
        ForgotPasswordRequest request = new ForgotPasswordRequest("john@example.com");
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(tokenService.createPasswordResetLinkToken(any())).thenReturn(resetLinkToken);
        when(tokenService.createPasswordResetOtp(any())).thenReturn(resetOtpToken);

        MessageResponse response = passwordService.forgotPassword(request);

        assertThat(response.message()).contains("Si cet email existe");
        verify(emailService).sendPasswordResetLink(eq("john@example.com"), eq("reset-link-uuid"));
        verify(emailService).sendPasswordResetOtp(eq("john@example.com"), eq("847291"));
    }

    @Test
    @DisplayName("forgotPassword — email inconnu : même réponse (anti-énumération)")
    void forgotPassword_unknownEmail_sameResponse() {
        ForgotPasswordRequest request = new ForgotPasswordRequest("unknown@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        MessageResponse response = passwordService.forgotPassword(request);

        // Même message que pour un email connu
        assertThat(response.message()).contains("Si cet email existe");
        verify(emailService, never()).sendPasswordResetLink(anyString(), anyString());
        verify(emailService, never()).sendPasswordResetOtp(anyString(), anyString());
    }

    // ══════════════════════════════════════════════════════════
    //  RESET BY LINK
    // ══════════════════════════════════════════════════════════

    @Test
    @DisplayName("resetPasswordByLink — succès : mot de passe changé")
    void resetPasswordByLink_success() {
        ResetPasswordRequest request = new ResetPasswordRequest(
                "reset-link-uuid", "newSecret123", "newSecret123");

        when(tokenService.validateToken("reset-link-uuid", TokenType.PASSWORD_RESET_LINK))
                .thenReturn(resetLinkToken);
        when(passwordEncoder.encode("newSecret123")).thenReturn("encodedNewPassword");
        when(userRepository.save(any())).thenReturn(user);

        MessageResponse response = passwordService.resetPasswordByLink(request);

        assertThat(response.message()).contains("réinitialisé");
        assertThat(user.getPassword()).isEqualTo("encodedNewPassword");
        verify(tokenService).markAsUsed(resetLinkToken);
        verify(tokenService).invalidateAllTokens(user, TokenType.PASSWORD_RESET_OTP);
    }

    @Test
    @DisplayName("resetPasswordByLink — échec : mots de passe différents")
    void resetPasswordByLink_passwordMismatch_throwsException() {
        ResetPasswordRequest request = new ResetPasswordRequest(
                "reset-link-uuid", "newSecret123", "differentPassword");

        assertThatThrownBy(() -> passwordService.resetPasswordByLink(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("correspondent pas");

        verify(tokenService, never()).validateToken(anyString(), any());
    }

    // ══════════════════════════════════════════════════════════
    //  RESET BY OTP
    // ══════════════════════════════════════════════════════════

    @Test
    @DisplayName("resetPasswordByOtp — succès : mot de passe changé")
    void resetPasswordByOtp_success() {
        ResetPasswordRequest request = new ResetPasswordRequest(
                "847291", "newSecret123", "newSecret123");

        when(tokenService.validateToken("847291", TokenType.PASSWORD_RESET_OTP))
                .thenReturn(resetOtpToken);
        when(passwordEncoder.encode("newSecret123")).thenReturn("encodedNewPassword");
        when(userRepository.save(any())).thenReturn(user);

        MessageResponse response = passwordService.resetPasswordByOtp(request);

        assertThat(response.message()).contains("réinitialisé");
        verify(tokenService).markAsUsed(resetOtpToken);
        verify(tokenService).invalidateAllTokens(user, TokenType.PASSWORD_RESET_LINK);
    }

    @Test
    @DisplayName("resetPasswordByOtp — échec : OTP invalide")
    void resetPasswordByOtp_invalidOtp_throwsException() {
        ResetPasswordRequest request = new ResetPasswordRequest(
                "000000", "newSecret123", "newSecret123");

        when(tokenService.validateToken("000000", TokenType.PASSWORD_RESET_OTP))
                .thenThrow(new RuntimeException("Token invalide"));

        assertThatThrownBy(() -> passwordService.resetPasswordByOtp(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("invalide");
    }
}
