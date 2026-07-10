package com.smartbilling.smartbilling.customer.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String CompanyName;

    @Column(unique= true, length = 14 )
    private String siret;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String address;

    private int phone;

    private String city;

    private String postalCode;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updateAt;

}