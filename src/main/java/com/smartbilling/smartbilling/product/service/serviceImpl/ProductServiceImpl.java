package com.smartbilling.smartbilling.product.service.serviceImpl;


import com.smartbilling.smartbilling.product.domain.Product;
import com.smartbilling.smartbilling.product.domain.ProductCategory;
import com.smartbilling.smartbilling.product.dto.requests.ProductRequest;
import com.smartbilling.smartbilling.product.dto.responses.ProductResponse;
import com.smartbilling.smartbilling.product.repository.ProductRepository;
import com.smartbilling.smartbilling.product.service.ProductService;
import com.smartbilling.smartbilling.shared.exception.DuplicateResourceException;
import com.smartbilling.smartbilling.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;


    @Override
    public ProductResponse create(ProductRequest request) {
        if (productRepository.existsByNameAndCategory(request.name(), request.category()))
            throw new DuplicateResourceException(
                    "Un produit avec ce nom existe déjà dans la catégorie " + request.category());

        Product product = Product.builder()
                .name(request.name())
                .priceHT(request.priceHT())
                .taxRate(request.taxRate())
                .description(request.description())
                .category(request.category())
                .build();

        return toResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Override
    public Page<ProductResponse> getAll(String search, ProductCategory category, Pageable pageable) {
        if (category != null)
            return productRepository.findByCategory(category, pageable).map(this::toResponse);
        if (search != null && !search.isBlank())
            return productRepository.search(search, pageable).map(this::toResponse);
        return productRepository.findAll(pageable).map(this::toResponse);

    }

    @Override
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findById(id);
        product.setName(request.name());
        product.setPriceHT(request.priceHT());
        product.setTaxRate(request.taxRate());
        product.setDescription(request.description());
        product.setCategory(request.category());
        return toResponse(productRepository.save(product));
    }

    @Override
    public void delete(Long id) {
        productRepository.delete(findById(id));
    }

    // Helpers

    private Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Produit introuvable avec l'id : " + id));
    }

    private BigDecimal calculatePriceTTC(BigDecimal priceHT, BigDecimal taxRate) {
        BigDecimal multiplier = BigDecimal.ONE.add(
                taxRate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        return priceHT.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
    }

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getPriceHT(),
                p.getTaxRate(),
                calculatePriceTTC(p.getPriceHT(), p.getTaxRate()),
                p.getDescription(),
                p.getCategory(),
                p.getCreatedAt()
        );
    }
}
