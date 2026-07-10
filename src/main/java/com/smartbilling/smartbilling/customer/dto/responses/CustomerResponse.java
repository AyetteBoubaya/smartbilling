package com.smartbilling.smartbilling.customer.dto.responses;

import java.time.LocalDateTime;

public record CustomerResponse(
        Long id,
        String companyName,
        String siret,
        String email,
        String phone,
        String address,
        String city,
        String postalCode,
        LocalDateTime createdAt
) {
}
