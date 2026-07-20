package com.smartbilling.smartbilling.auth.service.serviceImpl;

import com.smartbilling.smartbilling.auth.domain.Token;
import com.smartbilling.smartbilling.auth.domain.TokenType;
import com.smartbilling.smartbilling.auth.domain.User;
import com.smartbilling.smartbilling.auth.dto.requests.ForgotPasswordRequest;
import com.smartbilling.smartbilling.auth.dto.requests.ResetPasswordRequest;
import com.smartbilling.smartbilling.auth.dto.responses.MessageResponse;
import com.smartbilling.smartbilling.auth.repository.UserRepository;
import com.smartbilling.smartbilling.auth.service.EmailService;
import com.smartbilling.smartbilling.auth.service.PasswordService;
import com.smartbilling.smartbilling.auth.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordServiceImpl implements PasswordService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public MessageResponse forgotPassword(ForgotPasswordRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.email());

        // Sécurité : même réponse si l'email existe ou non (anti-énumération)
        if (userOpt.isEmpty()) {
            log.warn("Forgot password demandé pour email inconnu : {}", request.email());
            return new MessageResponse(
                    "Si cet email existe, vous recevrez un lien et un code de réinitialisation."
            );
        }

        User user = userOpt.get();

        // Génère les deux mécanismes en parallèle
        Token linkToken = tokenService.createPasswordResetLinkToken(user);
        Token otpToken  = tokenService.createPasswordResetOtp(user);

        // Envoie les deux dans un seul email
        emailService.sendPasswordResetLink(user.getEmail(), linkToken.getValue());
        emailService.sendPasswordResetOtp(user.getEmail(), otpToken.getValue());

        log.info("Reset password envoyé (lien + OTP) pour : {}", user.getEmail());
        return new MessageResponse(
                "Si cet email existe, vous recevrez un lien et un code de réinitialisation."
        );
    }

    @Override
    @Transactional
    public MessageResponse resetPasswordByLink(ResetPasswordRequest request) {
        validatePasswordMatch(request);

        Token token = tokenService.validateToken(request.token(), TokenType.PASSWORD_RESET_LINK);
        User user = token.getUser();

        applyNewPassword(user, request.newPassword());
        tokenService.markAsUsed(token);

        // Invalide aussi les OTP actifs pour ce user
        tokenService.invalidateAllTokens(user, TokenType.PASSWORD_RESET_OTP);

        log.info("Mot de passe réinitialisé via lien pour : {}", user.getEmail());
        return new MessageResponse("Mot de passe réinitialisé avec succès. Vous pouvez vous connecter.");
    }

    @Override
    @Transactional
    public MessageResponse resetPasswordByOtp(ResetPasswordRequest request) {
        validatePasswordMatch(request);

        Token token = tokenService.validateToken(request.token(), TokenType.PASSWORD_RESET_OTP);
        User user = token.getUser();

        applyNewPassword(user, request.newPassword());
        tokenService.markAsUsed(token);

        // Invalide aussi les liens actifs pour ce user
        tokenService.invalidateAllTokens(user, TokenType.PASSWORD_RESET_LINK);

        log.info("Mot de passe réinitialisé via OTP pour : {}", user.getEmail());
        return new MessageResponse("Mot de passe réinitialisé avec succès. Vous pouvez vous connecter.");
    }

    // ── Helpers ───────────────────────────────────────────────────

    private void validatePasswordMatch(ResetPasswordRequest request) {
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new RuntimeException("Les mots de passe ne correspondent pas");
        }
    }

    private void applyNewPassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}