package com.smartbilling.smartbilling.auth.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
    @Email(message ="Email invalide")
    @NotBlank(message = "Email obligatoire")
    String email,

    @NotBlank(message = "Mot de passe obligatoire")
    @Size(min= 6 , message ="Mot de passe minimum est 6 ")
    String password
    ) {}

