package com.smartbilling.smartbilling.auth.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        // token UUID (lien) OU code OTP 6 chiffres
        @NotBlank(message = "Token obligatoire")
        String token,

        @NotBlank(message = "Nouveau mot de passe obligatoire")
        @Size(min = 6, message = "Minimum 6 caractères")
        String newPassword,

        @NotBlank(message = "Confirmation obligatoire")
        String confirmPassword
) {}