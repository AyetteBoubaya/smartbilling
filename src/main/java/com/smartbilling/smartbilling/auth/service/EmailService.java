package com.smartbilling.smartbilling.auth.service;

public interface EmailService {
    void sendVerificationEmail(String toEmail, String token);
    void sendPasswordResetLink(String toEmail, String token);
    void sendPasswordResetOtp(String toEmail, String otp);
}