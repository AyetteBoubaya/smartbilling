package com.smartbilling.smartbilling.auth.controller;

import com.smartbilling.smartbilling.auth.dto.requests.LoginRequest;
import com.smartbilling.smartbilling.auth.dto.requests.UserRequest;
import com.smartbilling.smartbilling.auth.dto.responses.AuthResponse;
import com.smartbilling.smartbilling.auth.dto.responses.MessageResponse;
import com.smartbilling.smartbilling.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/register
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse register(@RequestBody @Valid UserRequest request) {
        return authService.register(request);
    }

    // POST /api/auth/login
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse login(@RequestBody @Valid LoginRequest request) {
        return authService.login(request);
    }

    // GET /api/auth/verify-email?token=xxxx
    @GetMapping("/verify-email")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponse verifyEmail(@RequestParam String token) {
        return authService.verifyEmail(token);
    }

    // POST /api/auth/resend-verification
    @PostMapping("/resend-verification")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponse resendVerification(@RequestParam String email) {
        return authService.resendVerificationEmail(email);
    }
}