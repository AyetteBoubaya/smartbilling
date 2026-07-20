package com.smartbilling.smartbilling.customer.dto.responses;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
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
