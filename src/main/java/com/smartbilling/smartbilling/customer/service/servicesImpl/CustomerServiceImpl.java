package com.smartbilling.smartbilling.customer.service.servicesImpl;

import com.smartbilling.smartbilling.customer.domain.Customer;
import com.smartbilling.smartbilling.customer.dto.requests.CustomerRequest;
import com.smartbilling.smartbilling.customer.dto.responses.CustomerResponse;
import com.smartbilling.smartbilling.customer.repository.CustomerRepository;
import com.smartbilling.smartbilling.customer.service.CustomerService;
import com.smartbilling.smartbilling.shared.exception.DuplicateResourceException;
import com.smartbilling.smartbilling.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private CustomerRepository customerRepository ;

    @Override
    public CustomerResponse create(CustomerRequest customerRequest) {
        if (customerRepository.existsByEmail(customerRequest.email())){
            throw new DuplicateResourceException("Un client avec cet email existe déjà");
        }
        if (customerRequest.siret() != null && customerRepository.existsBySiret(customerRequest.siret())){
            throw new DuplicateResourceException("Un client avec ce SIRET existe déjà");
        }
        Customer customer = Customer.builder()
                .companyName(customerRequest.companyName())
                .siret(customerRequest.siret())
                .email(customerRequest.email())
                .phone(customerRequest.phone())
                .address(customerRequest.address())
                .city(customerRequest.city())
                .postalCode(customerRequest.postalCode())
                .build();

        return toResponse(customerRepository.save(customer));
    }

    @Override
    public CustomerResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Override
    public Page<CustomerResponse> getAll(String search, Pageable pageable) {
        if(search != null && !search.isBlank())
            return customerRepository.search(search, pageable).map(this::toResponse);
        return customerRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public CustomerResponse update(Long id, CustomerRequest customerRequest) {
        Customer customer = findById(id);
        customer.setCompanyName(customerRequest.companyName());
        customer.setSiret(customerRequest.siret());
        customer.setEmail(customerRequest.email());
        customer.setPhone(customerRequest.phone());
        customer.setAddress(customerRequest.address());
        customer.setCity(customerRequest.city());
        customer.setPostalCode(customerRequest.postalCode());
        return toResponse(customerRepository.save(customer));
    }

    @Override
    public void delete(Long id) {
        customerRepository.delete(findById(id));
    }

    private Customer findById(Long id){
        return customerRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException(
                        "Client introuvable avec l'id : " + id));
    }

    private CustomerResponse toResponse(Customer c){
        return CustomerResponse.builder()
                .id(c.getId())
                .companyName(c.getCompanyName())
                .siret(c.getSiret())
                .email(c.getEmail())
                .phone(c.getPhone())
                .city(c.getCity())
                .postalCode(c.getPostalCode())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
