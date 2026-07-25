package com.smartbilling.smartbilling.invoice.dto.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record InvoiceRequest(

        @NotNull(message = "L'id du client est obligatoire")
        Long customerId,

        @NotEmpty(message = "La facture doit contenir au moins une ligne")
        @Valid
        List<InvoiceItemRequest> items,

        LocalDate issueDate,
        LocalDate dueDate,
        String notes
) {
}
