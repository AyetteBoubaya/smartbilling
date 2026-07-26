package com.smartbilling.smartbilling.invoice.service;

import com.smartbilling.smartbilling.invoice.domain.InvoiceStatus;
import com.smartbilling.smartbilling.invoice.dto.requests.InvoiceRequest;
import com.smartbilling.smartbilling.invoice.dto.requests.InvoiceStatusRequest;
import com.smartbilling.smartbilling.invoice.dto.responses.InvoiceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InvoiceService {
    InvoiceResponse create(InvoiceRequest request);
    InvoiceResponse getById(Long id);
    Page<InvoiceResponse> getAll(String search, InvoiceStatus status, Long customerId, Pageable pageable);
    InvoiceResponse updateStatus(Long id, InvoiceStatusRequest request);
    void delete(Long id);
    byte[] generatePdf(Long id);
}
