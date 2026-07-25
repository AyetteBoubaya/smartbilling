package com.smartbilling.smartbilling.invoice.domain;

import com.smartbilling.smartbilling.product.domain.Product;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name ="invoice_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="invoice_id" , nullable =false)
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 5, scale=2)
    private BigDecimal taxRate;

    // Calculés et persistés pour historisation
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalHT;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalTTC;

    @PrePersist
    @PreUpdate
    public void calculateTotals(){
        this.totalHT = unitPrice
                .multiply(BigDecimal.valueOf(quantity))
                .setScale(2, RoundingMode.HALF_UP);
        this.totalTTC = totalHT
                .multiply(BigDecimal.ONE.add(
                        taxRate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)));
    }

}
