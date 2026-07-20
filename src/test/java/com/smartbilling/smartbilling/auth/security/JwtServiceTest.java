package com.smartbilling.smartbilling.auth.security;

import com.smartbilling.smartbilling.auth.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JwtService — Tests unitaires")
class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET =
            "test-secret-key-minimum-256-bits-for-hmac-sha-algorithm-test";
    private static final String EMAIL = "john@example.com";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", 900_000L);
    }

    @Test
    @DisplayName("Génère un token valide contenant l'email")
    void generateToken_shouldContainEmail() {
        String token = jwtService.generateToken(EMAIL, true);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractEmail(token)).isEqualTo(EMAIL);
    }

    @Test
    @DisplayName("Extrait correctement emailVerified=true")
    void generateToken_emailVerifiedTrue() {
        String token = jwtService.generateToken(EMAIL, true);
        assertThat(jwtService.extractEmailVerified(token)).isTrue();
    }

    @Test
    @DisplayName("Extrait correctement emailVerified=false")
    void generateToken_emailVerifiedFalse() {
        String token = jwtService.generateToken(EMAIL, false);
        assertThat(jwtService.extractEmailVerified(token)).isFalse();
    }

    @Test
    @DisplayName("Valide un token correct pour le bon utilisateur")
    void isTokenValid_shouldReturnTrue_whenValidToken() {
        String token = jwtService.generateToken(EMAIL, true);
        UserDetails userDetails = buildUserDetails(EMAIL);

        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    @DisplayName("Rejette un token pour un autre utilisateur")
    void isTokenValid_shouldReturnFalse_whenWrongUser() {
        String token = jwtService.generateToken(EMAIL, true);
        UserDetails otherUser = buildUserDetails("other@example.com");

        assertThat(jwtService.isTokenValid(token, otherUser)).isFalse();
    }

    @Test
    @DisplayName("Rejette un token expiré")
    void isTokenValid_shouldReturnFalse_whenExpiredToken() {
        // Token avec expiration à -1ms (déjà expiré)
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", -1L);
        String token = jwtService.generateToken(EMAIL, true);
        UserDetails userDetails = buildUserDetails(EMAIL);

        assertThat(jwtService.isTokenValid(token, userDetails)).isFalse();
    }

    @Test
    @DisplayName("Retourne la durée d'expiration en secondes")
    void getExpirationSeconds_shouldReturn900() {
        assertThat(jwtService.getExpirationSeconds()).isEqualTo(900L);
    }

    // ── Helper ────────────────────────────────────────────────
    private UserDetails buildUserDetails(String email) {
        return User.builder()
                .username(email)
                .password("password")
                .authorities(Collections.emptyList())
                .build();
    }
}