package com.smartbilling.smartbilling.customer.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.extern.apachecommons.CommonsLog;
import org.hibernate.validator.constraints.UniqueElements;

import java.time.LocalDateTime;

@Entity
@Table(name="consumers")
@Data
public class Consumer {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private String name;

    @Email
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Address is required")
    private String address;

    private LocalDateTime CreatedAt;

}
