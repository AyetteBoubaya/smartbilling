package com.smartbilling.smartbilling.invoice.controller;

import com.smartbilling.smartbilling.invoice.domain.Invoice;
import com.smartbilling.smartbilling.invoice.domain.InvoiceStatus;
import com.smartbilling.smartbilling.invoice.dto.requests.InvoiceRequest;
import com.smartbilling.smartbilling.invoice.dto.requests.InvoiceStatusRequest;
import com.smartbilling.smartbilling.invoice.dto.responses.InvoiceResponse;
import com.smartbilling.smartbilling.invoice.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static jakarta.mail.event.FolderEvent.CREATED;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@Tag(name="Factures", description = "Gestion des factures")
@SecurityRequirement(name = "bearerAuth")
public class Invoicecontroller {
    private final InvoiceService invoiceService;

    @PostMapping
    @PreAuthorize("hasRole('Admin')")
    @Operation(summary = "Créer une facture")
    public ResponseEntity<InvoiceResponse> create(@RequestBody @Valid InvoiceRequest request) {
        return ResponseEntity.status(CREATED).body(invoiceService.create(request));
    }

    @GetMapping
    @Operation(summary = "Lister les factures avec filtres et pagination")
    public ResponseEntity<Page<InvoiceResponse>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) InvoiceStatus status,
            @RequestParam(required = false) Long customerId,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(invoiceService.getAll(search, status, customerId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une facture par ID")
    public ResponseEntity<InvoiceResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getById(id));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('Admin')")
    @Operation(summary = "Changer le statut d'une facture")
    public ResponseEntity<InvoiceResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody @Valid InvoiceStatusRequest request) {
        return ResponseEntity.ok(invoiceService.updateStatus(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    @Operation(summary = "Supprimer une facture (DRAFT uniquement)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        invoiceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/pdf")
    @Operation(summary = "Générer le PDF d'une facture")
    public ResponseEntity<byte[]> generatePdf(@PathVariable Long id) {
        byte[] pdf = invoiceService.generatePdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=facture-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
