package com.smartbilling.smartbilling.customer.service;

import com.smartbilling.smartbilling.customer.dto.requests.CustomerRequest;
import com.smartbilling.smartbilling.customer.dto.responses.CustomerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {
    CustomerResponse create(CustomerRequest customerRequest);
    CustomerResponse getById(Long id);
    Page<CustomerResponse> getAll(String search, Pageable pageable);
    CustomerResponse update(Long id, CustomerRequest customerRequest);
    void delete(Long id);

}
