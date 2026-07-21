package com.smartbilling.smartbilling.product.service;

import com.smartbilling.smartbilling.product.domain.ProductCategory;
import com.smartbilling.smartbilling.product.dto.requests.ProductRequest;
import com.smartbilling.smartbilling.product.dto.responses.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductResponse create(ProductRequest request);
    ProductResponse getById(Long id);
    Page<ProductResponse> getAll(String search, ProductCategory category, Pageable pageable);
    ProductResponse update(Long id, ProductRequest request);
    void delete(Long id);
}
