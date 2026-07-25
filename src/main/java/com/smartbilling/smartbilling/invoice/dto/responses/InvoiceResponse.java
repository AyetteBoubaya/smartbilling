package com.smartbilling.smartbilling.invoice.dto.responses;

import com.smartbilling.smartbilling.invoice.domain.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record InvoiceResponse(
        Long id,
        String invoiceNumber,
        Long customerId,
        String customerName,
        List<InvoiceItemResponse> items,
        InvoiceStatus status,
        LocalDate issueDate,
        LocalDate dueDate,
        BigDecimal totalHT,
        BigDecimal totalTTC,
        String notes,
        LocalDateTime createdAt
) {
}
