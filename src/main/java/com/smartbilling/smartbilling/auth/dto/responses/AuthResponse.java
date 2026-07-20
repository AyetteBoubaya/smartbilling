package com.smartbilling.smartbilling.auth.dto.responses;

import com.smartbilling.smartbilling.auth.domain.Role;

public record AuthResponse(
        String accessToken,
        String refreshToken,    // ← ajouté
        String tokenType,
        long expiresIn,         // ← ajouté (secondes)
        Role role,
        boolean emailVerified,
        String message
) {
    public AuthResponse(String accessToken, String refreshToken,
                        long expiresIn, Role role, boolean emailVerified) {
        this(
                accessToken,
                refreshToken,
                "Bearer",
                expiresIn,
                role,
                emailVerified,
                emailVerified
                        ? "Connexion réussie."
                        : "Connexion réussie. Pensez à vérifier votre adresse email."
        );
    }
}