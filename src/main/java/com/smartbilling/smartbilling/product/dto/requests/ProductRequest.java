package com.smartbilling.smartbilling.product.dto.requests;

import com.smartbilling.smartbilling.product.domain.ProductCategory;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank(message= "Le nom du produit est obligatoire")
        String name,

        @NotBlank(message = "Le prixHT est obligatoire")
        @DecimalMin(
                value="0.0",
                inclusive = false,
                message= "Le prix doit etre supérieur à 0"
        )
        BigDecimal priceHT,

        @NotNull(message = "Le taux de TVA est obligatoire")
        @DecimalMin(
                value = "0.0",
                message = "La TVA ne peut pas etre négative"
        )
        @DecimalMax(
                value="100.0",
                message = "La TVA ne peut pas dépasser 100%"
        )
        BigDecimal taxRate,

        @Size(
                max = 500,
                message = "La description ne ppeut pas etre dépasser 500 caractéres"
        )
        String description,

        @NotNull(message = "La catégorie est obligatoire")
        ProductCategory category



) {
}
