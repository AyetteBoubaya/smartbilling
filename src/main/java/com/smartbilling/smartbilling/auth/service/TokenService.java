package com.smartbilling.smartbilling.auth.service;

import com.smartbilling.smartbilling.auth.domain.Token;
import com.smartbilling.smartbilling.auth.domain.TokenType;
import com.smartbilling.smartbilling.auth.domain.User;

public interface TokenService {
    Token createVerificationToken(User user);
    Token createPasswordResetLinkToken(User user);
    Token createPasswordResetOtp(User user);
    Token validateToken(String value, TokenType type);
    void markAsUsed(Token token);
    void invalidateAllTokens(User user, TokenType type);
}