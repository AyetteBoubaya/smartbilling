package com.smartbilling.smartbilling.auth.service;

import com.smartbilling.smartbilling.auth.domain.RefreshToken;
import com.smartbilling.smartbilling.auth.domain.User;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user);
    RefreshToken validateRefreshToken(String value);
    void revokeAllUserTokens(User user);
}