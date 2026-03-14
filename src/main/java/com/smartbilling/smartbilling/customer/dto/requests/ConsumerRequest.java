package com.smartbilling.smartbilling.customer.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public record ConsumerRequest(

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Address is required")
        String address
) {}
