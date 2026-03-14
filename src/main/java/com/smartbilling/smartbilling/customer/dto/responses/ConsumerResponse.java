package com.smartbilling.smartbilling.customer.dto.responses;

import lombok.Data;

import java.time.LocalDateTime;


public record ConsumerResponse(
        Long id,
        String name,
        String email,
        String address,
        LocalDateTime createdAt
) {}
