package com.smartbilling.smartbilling.product.dto.responses;

import com.smartbilling.smartbilling.product.domain.ProductCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String name,
        BigDecimal priceHT,
        BigDecimal taxRate,
        BigDecimal priceTTC,
        String description,
        ProductCategory category,
        LocalDateTime createdAt
) {
}
