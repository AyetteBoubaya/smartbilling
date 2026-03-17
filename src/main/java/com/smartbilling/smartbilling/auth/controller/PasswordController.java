package com.smartbilling.smartbilling.auth.controller;

import com.smartbilling.smartbilling.auth.dto.requests.ForgotPasswordRequest;
import com.smartbilling.smartbilling.auth.dto.requests.ResetPasswordRequest;
import com.smartbilling.smartbilling.auth.dto.responses.MessageResponse;
import com.smartbilling.smartbilling.auth.service.PasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordService passwordService;

    // POST /api/auth/forgot-password
    // Envoie un lien ET un OTP par email
    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponse forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        return passwordService.forgotPassword(request);
    }

    // POST /api/auth/reset-password/link
    // Reset via le token UUID reçu dans le lien email
    @PostMapping("/reset-password/link")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponse resetByLink(@RequestBody @Valid ResetPasswordRequest request) {
        return passwordService.resetPasswordByLink(request);
    }

    // POST /api/auth/reset-password/otp
    // Reset via le code OTP à 6 chiffres reçu par email
    @PostMapping("/reset-password/otp")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponse resetByOtp(@RequestBody @Valid ResetPasswordRequest request) {
        return passwordService.resetPasswordByOtp(request);
    }
}