package com.smartbilling.smartbilling.invoice.domain;

import com.smartbilling.smartbilling.customer.domain.Customer;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String invoiceNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InvoiceItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private InvoiceStatus status = InvoiceStatus.DRAFT;

    private LocalDate issueDate;
    private LocalDate dueDate;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalHT;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalTVA;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalTTC;

    private String notes;

    @CreationTimestamp
    private LocalDateTime createdAt;
}


