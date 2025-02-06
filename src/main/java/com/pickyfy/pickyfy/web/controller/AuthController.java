package com.pickyfy.pickyfy.web.controller;

import com.pickyfy.pickyfy.service.AuthService;
import com.pickyfy.pickyfy.web.apiResponse.common.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerApi {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private final AuthService authService;

    @Override
    public ApiResponse<String> logout(
            @CookieValue(value = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (refreshToken != null) {
            authService.logout(refreshToken);
        }
        clearRefreshTokenCookie(response);
        return ApiResponse.onSuccess("로그아웃에 성공했습니다.");
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie expiredCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());
    }
}
