package com.smartbilling.smartbilling.auth.exception;

import com.smartbilling.smartbilling.auth.dto.responses.ErrorResponse;
import com.smartbilling.smartbilling.auth.dto.responses.ValidationErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // ── Validation @Valid → 400 avec détail par champ ─────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fields = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            fields.put(field, message);
        });
        return new ValidationErrorResponse(fields);
    }

    // ── Mauvais email / mot de passe → 401 ────────────────────
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleBadCredentials(BadCredentialsException ex) {
        return new ErrorResponse(401, "Non autorisé", "Email ou mot de passe incorrect");
    }

    // ── Compte désactivé → 403 ────────────────────────────────
    @ExceptionHandler(DisabledException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleDisabled(DisabledException ex) {
        return new ErrorResponse(403, "Accès refusé", "Compte désactivé. Contactez l'administrateur.");
    }

    // ── Erreurs métier (RuntimeException) → 400 ───────────────
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleRuntimeException(RuntimeException ex) {
        log.error("Erreur métier : {}", ex.getMessage());
        return new ErrorResponse(400, "Erreur", ex.getMessage());
    }

    // ── Erreur inattendue → 500 ────────────────────────────────
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex) {
        log.error("Erreur inattendue : {}", ex.getMessage(), ex);
        return new ErrorResponse(500, "Erreur serveur", "Une erreur inattendue s'est produite");
    }
}