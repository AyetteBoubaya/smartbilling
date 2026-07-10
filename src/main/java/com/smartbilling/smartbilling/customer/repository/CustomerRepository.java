package com.smartbilling.smartbilling.customer.repository;

import ch.qos.logback.core.net.server.Client;
import com.smartbilling.smartbilling.customer.domain.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
    boolean existsByEmail(String email);
    boolean existsBySiret(String siret);

    //Recherche sur nom ou email(insensible à la casse)
    @Query("""
        SELECT c FROM Client c
        WHERE LOWER(c.companyName) LIKE LOWER(CONCAT('%',:search,'%'))
        OR LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%'))
        """)
    Page<Client> search(@Param("search") String search, Pageable pageable);
}
