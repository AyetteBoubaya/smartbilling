package com.smartbilling.smartbilling.auth.dto.responses;

import com.smartbilling.smartbilling.auth.domain.Role;

public record UserResponse(Long id , String email , Role role) {
}
