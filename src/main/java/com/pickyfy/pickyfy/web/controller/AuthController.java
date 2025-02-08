package com.pickyfy.pickyfy.web.controller;

import com.pickyfy.pickyfy.common.Constant;
import com.pickyfy.pickyfy.service.AuthService;
import com.pickyfy.pickyfy.web.apiResponse.common.ApiResponse;
import com.pickyfy.pickyfy.web.dto.response.AuthResponse;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.Duration;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerApi {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private final AuthService authService;

    @Override
    public ApiResponse<String> logout(
            @Parameter(hidden = true) @CookieValue(value = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (refreshToken != null) {
            authService.logout(refreshToken);
        }
        clearCookie(response);
        return ApiResponse.onSuccess("로그아웃에 성공했습니다.");
    }

    @Override
    public ApiResponse<String> reIssue(
            @Parameter(hidden = true) @CookieValue(value = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken,
            HttpServletResponse response) {

        if (refreshToken == null) {
            return ApiResponse.onFailure("400","리프레시 토큰이 없습니다.", null);
        }
        AuthResponse authResponse = authService.reIssue(refreshToken);
        response.setHeader("Authorization", "Bearer " + authResponse.accessToken());
        createCookie(response, authResponse.refreshToken());

        return ApiResponse.onSuccess("토큰 재발급 완료.");
    }

    private void createCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie expiredCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofMillis(Constant.REFRESH_TOKEN_EXPIRATION_TIME).getSeconds())
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());
    }

    private void clearCookie(HttpServletResponse response) {
        ResponseCookie expiredCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());
    }
}
