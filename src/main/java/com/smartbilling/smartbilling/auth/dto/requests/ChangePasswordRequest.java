package com.smartbilling.smartbilling.auth.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(

        @NotBlank(message = "Mot de passe actuel obligatoire")
        String currentPassword,

        @NotBlank(message = "Nouveau mot de passe obligatoire")
        @Size(min = 6, message = "Le mot de passe doit contenir au minimum 6 caractères")
        String newPassword,

        @NotBlank(message = "Confirmation du mot de passe obligatoire")
        String confirmPassword
) {}