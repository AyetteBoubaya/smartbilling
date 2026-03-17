package com.smartbilling.smartbilling.auth.service.serviceImpl;

import com.smartbilling.smartbilling.auth.domain.Token;
import com.smartbilling.smartbilling.auth.domain.TokenType;
import com.smartbilling.smartbilling.auth.domain.User;
import com.smartbilling.smartbilling.auth.repository.TokenRepository;
import com.smartbilling.smartbilling.auth.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;

    // SecureRandom pour l'OTP (cryptographiquement sûr)
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public Token createVerificationToken(User user) {
        // Supprime les anciens tokens de vérification pour ce user
        tokenRepository.deleteAllByUserAndType(user, TokenType.EMAIL_VERIFICATION);

        Token token = new Token();
        token.setUser(user);
        token.setValue(UUID.randomUUID().toString());
        token.setType(TokenType.EMAIL_VERIFICATION);
        token.setExpiresAt(LocalDateTime.now().plusHours(24));
        return tokenRepository.save(token);
    }

    @Override
    public Token createPasswordResetLinkToken(User user) {
        tokenRepository.deleteAllByUserAndType(user, TokenType.PASSWORD_RESET_LINK);

        Token token = new Token();
        token.setUser(user);
        token.setValue(UUID.randomUUID().toString());
        token.setType(TokenType.PASSWORD_RESET_LINK);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        return tokenRepository.save(token);
    }

    @Override
    public Token createPasswordResetOtp(User user) {
        tokenRepository.deleteAllByUserAndType(user, TokenType.PASSWORD_RESET_OTP);

        Token token = new Token();
        token.setUser(user);
        token.setValue(generateOtp());
        token.setType(TokenType.PASSWORD_RESET_OTP);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        return tokenRepository.save(token);
    }

    @Override
    public Token validateToken(String value, TokenType type) {
        Token token = tokenRepository.findByValueAndType(value, type)
                .orElseThrow(() -> new RuntimeException("Token invalide"));

        if (token.isUsed()) {
            throw new RuntimeException("Token déjà utilisé");
        }
        if (token.isExpired()) {
            throw new RuntimeException("Token expiré");
        }
        return token;
    }

    @Override
    public void markAsUsed(Token token) {
        token.setUsed(true);
        tokenRepository.save(token);
    }

    @Override
    public void invalidateAllTokens(User user, TokenType type) {
        tokenRepository.invalidateAllByUserAndType(user, type);
    }

    // ---------------------------------------------------------------
    private String generateOtp() {
        int otp = 100_000 + secureRandom.nextInt(900_000); // 100000 à 999999
        return String.valueOf(otp);
    }
}