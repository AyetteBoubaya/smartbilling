package com.smartbilling.smartbilling.auth.dto.responses;

public record AuthResponse(
        String accessToken,
        String tokenType,
        boolean emailVerified   // frontend affiche avertissement si false
) {
    public AuthResponse(String accessToken, boolean emailVerified) {
        this(accessToken, "Bearer", emailVerified);
    }
}