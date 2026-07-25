package com.smartbilling.smartbilling.invoice.dto.requests;

import com.smartbilling.smartbilling.invoice.domain.InvoiceStatus;
import jakarta.validation.constraints.NotNull;

public record InvoiceStatusRequest(
        @NotNull(message = "Le statut est obligatoire")
        InvoiceStatus status
) {
}
