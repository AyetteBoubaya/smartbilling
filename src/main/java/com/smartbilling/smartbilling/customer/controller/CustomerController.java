package com.smartbilling.smartbilling.customer.controller;

import com.smartbilling.smartbilling.customer.dto.requests.CustomerRequest;
import com.smartbilling.smartbilling.customer.dto.responses.CustomerResponse;
import com.smartbilling.smartbilling.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customers" , description = "Gestion de Clients")
@SecurityRequirement(name = "bearerAuth")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer un client")
    public ResponseEntity<CustomerResponse> create( @RequestBody @Valid CustomerRequest customerRequest){
        return ResponseEntity.status(CREATED).body(customerService.create(customerRequest));
    }

    @GetMapping
    @Operation(summary = "Lister les clients avec pagination et recherche ")
    public ResponseEntity<Page<CustomerResponse>> getAll(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10, sort = "companyName" )Pageable pageable){
        return  ResponseEntity.ok(customerService.getAll(search, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un client par ID")
    public ResponseEntity<CustomerResponse> getById(@PathVariable Long id){
        return ResponseEntity.ok(customerService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Modifier un client")
    public ResponseEntity<CustomerResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid CustomerRequest request) {
        return ResponseEntity.ok(customerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer un client")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
