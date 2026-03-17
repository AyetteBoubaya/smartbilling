package com.smartbilling.smartbilling.auth.service.serviceImpl;

import com.smartbilling.smartbilling.auth.domain.Role;
import com.smartbilling.smartbilling.auth.domain.Token;
import com.smartbilling.smartbilling.auth.domain.TokenType;
import com.smartbilling.smartbilling.auth.domain.User;
import com.smartbilling.smartbilling.auth.dto.requests.LoginRequest;
import com.smartbilling.smartbilling.auth.dto.requests.UserRequest;
import com.smartbilling.smartbilling.auth.dto.responses.AuthResponse;
import com.smartbilling.smartbilling.auth.dto.responses.MessageResponse;
import com.smartbilling.smartbilling.auth.repository.UserRepository;
import com.smartbilling.smartbilling.auth.security.JwtService;
import com.smartbilling.smartbilling.auth.service.AuthService;
import com.smartbilling.smartbilling.auth.service.EmailService;
import com.smartbilling.smartbilling.auth.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final EmailService emailService;

    @Override
    @Transactional
    public MessageResponse register(UserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password())); // ← BCrypt
        user.setRole(Role.User);
        user.setEmailVerified(false);
        user.setEnabled(true);
        userRepository.save(user);

        // Génère et envoie le token de vérification
        Token verificationToken = tokenService.createVerificationToken(user);
        emailService.sendVerificationEmail(user.getEmail(), verificationToken.getValue());

        log.info("Nouvel utilisateur enregistré : {}", user.getEmail());
        return new MessageResponse("Inscription réussie. Vérifiez votre email pour activer votre compte.");
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // Spring Security vérifie email + password (+ isEnabled)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        String token = jwtService.generateToken(user.getEmail(), user.isEmailVerified());

        // emailVerified=false → le frontend affiche l'avertissement
        return new AuthResponse(token, user.isEmailVerified());
    }

    @Override
    @Transactional
    public MessageResponse verifyEmail(String tokenValue) {
        Token token = tokenService.validateToken(tokenValue, TokenType.EMAIL_VERIFICATION);

        User user = token.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        tokenService.markAsUsed(token);

        log.info("Email vérifié pour : {}", user.getEmail());
        return new MessageResponse("Email vérifié avec succès. Vous pouvez vous connecter.");
    }

    @Override
    public MessageResponse resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (user.isEmailVerified()) {
            return new MessageResponse("Votre email est déjà vérifié.");
        }

        Token token = tokenService.createVerificationToken(user);
        emailService.sendVerificationEmail(user.getEmail(), token.getValue());
        return new MessageResponse("Email de vérification renvoyé.");
    }
}