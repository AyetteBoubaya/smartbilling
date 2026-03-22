package com.smartbilling.smartbilling.auth.dto.responses;

public record RefreshTokenResponse(
        String accessToken,
        String refreshToken,    // nouveau refresh token (rotation)
        String tokenType,       // toujours "Bearer"
        long expiresIn          // durée de vie de l'accessToken en secondes
) {
    public RefreshTokenResponse(String accessToken, String refreshToken, long expiresIn) {
        this(accessToken, refreshToken, "Bearer", expiresIn);
    }
}