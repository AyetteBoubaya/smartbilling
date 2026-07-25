package com.smartbilling.smartbilling.invoice.dto.requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record InvoiceItemRequest(
        @NotNull(message = "L'id du produit est obligatoire")
        Long productId,
        @NotNull(message = "La quantité est obligatoire")
        @Min(value= 1, message = "La quantitédoit etre au moins 1")
        Integer quantity,

        //Prix unitaire optionnel - si absent, on prend le prix du produit
        @DecimalMin(value ="0.0", inclusive = false, message = "Le prix doit etre > 0")
        BigDecimal unitPrice
) {
}
