package com.smartbilling.smartbilling.customer.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;


public record CustomerRequest(
        @NotBlank(message = "Le nom de l'entreprise est obligatoire") String companyName,
        @Pattern(regexp = "\\d{14}", message = "Le Siret doit contenir 14 chiffres") String siret,
        @NotBlank(message = "l'email est obligatoire")
        @Email(message = "format email invalide") String email,
        String phone,
        String address,
        String city,
        String postalCode
) {
}
