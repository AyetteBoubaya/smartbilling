package com.smartbilling.smartbilling.invoice.dto.responses;

import java.math.BigDecimal;

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
