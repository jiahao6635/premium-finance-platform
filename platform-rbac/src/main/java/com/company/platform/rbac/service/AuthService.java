package com.company.platform.rbac.service;

import com.company.platform.rbac.model.dto.LoginRequest;
import com.company.platform.rbac.model.dto.TokenResponse;

public interface AuthService {
    TokenResponse login(LoginRequest request);

    TokenResponse refresh(String refreshToken);

    void logout(String bearerToken);
}
