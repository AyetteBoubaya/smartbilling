package com.smartbilling.smartbilling.auth.service.serviceImpl;

import com.smartbilling.smartbilling.auth.domain.RefreshToken;
import com.smartbilling.smartbilling.auth.domain.User;
import com.smartbilling.smartbilling.auth.repository.RefreshTokenRepository;
import com.smartbilling.smartbilling.auth.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        // Supprime les anciens tokens de ce user avant d'en créer un nouveau
        refreshTokenRepository.deleteAllByUser(user);

        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setValue(UUID.randomUUID().toString());
        token.setExpiresAt(LocalDateTime.now()
                .plusSeconds(refreshExpirationMs / 1000));
        token.setRevoked(false);

        return refreshTokenRepository.save(token);
    }

    @Override
    public RefreshToken validateRefreshToken(String value) {
        RefreshToken token = refreshTokenRepository.findByValue(value)
                .orElseThrow(() -> new RuntimeException("Refresh token invalide"));

        // ── Détection de réutilisation ─────────────────────────
        // Si le token est révoqué → quelqu'un a peut-être volé un ancien token
        // On révoque TOUS les tokens du user par sécurité
        if (token.isRevoked()) {
            log.warn("Tentative de réutilisation d'un refresh token révoqué pour : {}",
                    token.getUser().getEmail());
            revokeAllUserTokens(token.getUser());
            throw new RuntimeException(
                    "Session invalide. Veuillez vous reconnecter."
            );
        }

        if (token.isExpired()) {
            throw new RuntimeException("Session expirée. Veuillez vous reconnecter.");
        }

        return token;
    }

    @Override
    @Transactional
    public void revokeAllUserTokens(User user) {
        refreshTokenRepository.revokeAllByUser(user);
        log.info("Tous les refresh tokens révoqués pour : {}", user.getEmail());
    }
}