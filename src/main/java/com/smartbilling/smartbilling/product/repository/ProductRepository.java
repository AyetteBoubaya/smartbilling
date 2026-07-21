package com.smartbilling.smartbilling.product.repository;

import com.smartbilling.smartbilling.product.domain.Product;
import com.smartbilling.smartbilling.product.domain.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;


public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByNameAndCategory(String name, ProductCategory category);

    // Recherche par nom ou description
    @Query("""
            SELECT p FROM Product p
            WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%'))
            """)
    Page<Product> search(@Param("search") String search, Pageable pageable);

    Page<Product> findByCategory(ProductCategory category, Pageable pageable);

}
