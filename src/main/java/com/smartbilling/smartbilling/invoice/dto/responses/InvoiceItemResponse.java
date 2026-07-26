package com.smartbilling.smartbilling.invoice.dto.responses;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record InvoiceItemResponse(
        Long id,
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal taxRate,
        BigDecimal totalHt,
        BigDecimal totalTTC

) {
}
