package com.smartbilling.smartbilling.auth.dto.requests;

import jakarta.validation.constraints.NotBlank;

public record VerifyEmailRequest(

        @NotBlank(message = "Token de vérification obligatoire")
        String token
) {}