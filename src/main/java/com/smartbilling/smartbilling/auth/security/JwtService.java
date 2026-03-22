package com.smartbilling.smartbilling.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@Slf4j
public class JwtService {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    // ── Génération ────────────────────────────────────────────

    public String generateToken(String email, boolean emailVerified) {
        return Jwts.builder()
                .subject(email)
                .claim("emailVerified", emailVerified)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    // ── Extraction ────────────────────────────────────────────

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean extractEmailVerified(String token) {
        return (boolean) extractClaims(token).get("emailVerified");
    }

    // Expose la durée en secondes pour le champ expiresIn de AuthResponse
    public long getExpirationSeconds() {
        return jwtExpirationMs / 1000;
    }

    // ── Validation ────────────────────────────────────────────

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String email = extractEmail(token);
            return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (Exception e) {
            log.warn("JWT invalide : {}", e.getMessage());
            return false;
        }
    }

    // ── Privé ─────────────────────────────────────────────────

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}