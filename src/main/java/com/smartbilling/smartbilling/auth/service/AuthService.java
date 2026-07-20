package com.smartbilling.smartbilling.auth.service;

import com.smartbilling.smartbilling.auth.dto.requests.LoginRequest;
import com.smartbilling.smartbilling.auth.dto.requests.RefreshTokenRequest;
import com.smartbilling.smartbilling.auth.dto.requests.UserRequest;
import com.smartbilling.smartbilling.auth.dto.responses.AuthResponse;
import com.smartbilling.smartbilling.auth.dto.responses.MessageResponse;
import com.smartbilling.smartbilling.auth.dto.responses.RefreshTokenResponse;

public interface AuthService {
    MessageResponse register(UserRequest request);
    AuthResponse login(LoginRequest request);
    MessageResponse verifyEmail(String token);
    MessageResponse resendVerificationEmail(String email);
    RefreshTokenResponse refresh(RefreshTokenRequest request);   // ← nouveau
    MessageResponse logout(RefreshTokenRequest request);
}