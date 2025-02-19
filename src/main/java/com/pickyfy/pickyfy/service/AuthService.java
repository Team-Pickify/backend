package com.pickyfy.pickyfy.service;

import com.pickyfy.pickyfy.web.dto.response.AuthResponse;

public interface AuthService {
    void logout(String refreshToken);
    AuthResponse reIssue(String refreshToken);
    boolean isAuthenticated(String accessToken);
}
