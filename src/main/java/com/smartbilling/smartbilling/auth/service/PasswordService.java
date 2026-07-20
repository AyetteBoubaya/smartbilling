package com.smartbilling.smartbilling.auth.service;

import com.smartbilling.smartbilling.auth.dto.requests.ForgotPasswordRequest;
import com.smartbilling.smartbilling.auth.dto.requests.ResetPasswordRequest;
import com.smartbilling.smartbilling.auth.dto.responses.MessageResponse;

public interface PasswordService {
    MessageResponse forgotPassword(ForgotPasswordRequest request);
    MessageResponse resetPasswordByLink(ResetPasswordRequest request);
    MessageResponse resetPasswordByOtp(ResetPasswordRequest request);
}