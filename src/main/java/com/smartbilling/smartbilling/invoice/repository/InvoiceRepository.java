package com.smartbilling.smartbilling.invoice.repository;

import com.smartbilling.smartbilling.invoice.domain.Invoice;
import com.smartbilling.smartbilling.invoice.domain.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    // pour générer le numéro auto(INV-2024-0001)
    long countByInvoiceNumberStartingWith(String prefix);

    Page<Invoice> findByStatus(InvoiceStatus status, Pageable pageable);
    Page<Invoice> findByCustomerId(Long customerId, Pageable pageable);

    @Query("""
SELECT i FROM Invoice in 
WHERE LOWER(i.invoiceNumber) LIKE LOWER(CONCAT('%', :search, '%'))
OR LOWER(i.customer.companyName) LIKE LOWER(CONCAT('%', :search, '%'))
 
""")
    Page<Invoice> search(@Param("search") String search, Pageable pageable);
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
}
