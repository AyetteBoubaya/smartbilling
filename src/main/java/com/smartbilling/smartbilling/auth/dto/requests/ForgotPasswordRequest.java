package com.smartbilling.smartbilling.auth.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
        @Email(message = "Email invalide")
        @NotBlank(message = "Email obligatoire")
        String email
) {}