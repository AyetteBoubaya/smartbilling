package com.smartbilling.smartbilling.customer.repository;


import com.smartbilling.smartbilling.customer.domain.Consumer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsumerRepository extends JpaRepository<Consumer,Long> {
    Optional<Consumer> findByEmail(String email);
    Boolean existsByEmail(String email);
}
