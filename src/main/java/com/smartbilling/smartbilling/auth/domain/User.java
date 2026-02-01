package com.smartbilling.smartbilling.auth.domain;


import jakarta.persistence.*;
import lombok.Data;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;

@Entity
@Table(name ="users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false , unique = true)
    private String email;


    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private LocalDateTime createsAt;
    private LocalDateTime updatesAt;

    @PrePersist
    public void prePersist() {
        this.createsAt = LocalDateTime.now();
        this.updatesAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatesAt = LocalDateTime.now();
    }
}
