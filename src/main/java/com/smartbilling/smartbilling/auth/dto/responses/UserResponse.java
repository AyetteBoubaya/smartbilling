package com.smartbilling.smartbilling.auth.dto.responses;

import com.smartbilling.smartbilling.auth.domain.Role;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String email,
        Role role,
        boolean emailVerified,
        LocalDateTime createdAt
) {
    // Constructeur court pour rétrocompatibilité avec le code existant
    public UserResponse(Long id, String email, Role role) {
        this(id, email, role, false, null);
    }
}