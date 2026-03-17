package com.smartbilling.smartbilling.auth.dto.responses;

import java.time.LocalDateTime;
import java.util.Map;

public record ValidationErrorResponse(
        int status,
        String error,
        Map<String, String> fields,   // { "email": "Email invalide", "password": "..." }
        LocalDateTime timestamp
) {
    public ValidationErrorResponse(Map<String, String> fields) {
        this(400, "Erreur de validation", fields, LocalDateTime.now());
    }
}