package com.smartbilling.smartbilling.customer.service.servicesImpl;

import com.smartbilling.smartbilling.customer.dto.requests.CustomerRequest;
import com.smartbilling.smartbilling.customer.dto.responses.CustomerResponse;
import com.smartbilling.smartbilling.customer.service.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class CustomerServiceImpl implements CustomerService {
    @Override
    public CustomerResponse create(CustomerRequest customerRequest) {
        return null;
    }

    @Override
    public CustomerResponse getById(Long id) {
        return null;
    }

    @Override
    public Page<CustomerResponse> getAll(String search, Pageable pageable) {
        return null;
    }

    @Override
    public CustomerResponse update(Long id, CustomerRequest customerRequest) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
