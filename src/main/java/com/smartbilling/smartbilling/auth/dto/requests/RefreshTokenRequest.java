package com.smartbilling.smartbilling.auth.dto.requests;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token obligatoire")
        String refreshToken
) {}