package com.pickyfy.pickyfy.web.controller;

import com.pickyfy.pickyfy.common.Constant;
import com.pickyfy.pickyfy.service.AuthService;
import com.pickyfy.pickyfy.web.apiResponse.common.ApiResponse;
import com.pickyfy.pickyfy.web.apiResponse.success.SuccessStatus;
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
    private static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
    private final AuthService authService;

    @Override
    public ApiResponse<Void> logout(
            @Parameter(hidden = true) @CookieValue(value = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (refreshToken != null) {
            authService.logout(refreshToken);
        }
        clearCookie(response);
        return ApiResponse.onSuccess(SuccessStatus.LOGOUT_SUCCESS, null);
    }

    @Override
    public ApiResponse<Void> reIssue(
            @Parameter(hidden = true) @CookieValue(value = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken,
            HttpServletResponse response) {

        if (refreshToken == null) {
            return ApiResponse.onFailure("400","리프레시 토큰이 없습니다.", null);
        }
        AuthResponse authResponse = authService.reIssue(refreshToken);
        response.setHeader("Authorization", "Bearer " + authResponse.accessToken());
        createCookie(response, authResponse.refreshToken());

        return ApiResponse.onSuccess(SuccessStatus.REISSUE_TOKEN_SUCCESS, null);
    }

    @Override
    public ApiResponse<Boolean> isAuthenticated(
            @Parameter(hidden = true) @CookieValue(name = "accessToken", required = false) String accessToken
    ) {
        boolean isAuthenticated = authService.isAuthenticated(accessToken);
        return ApiResponse.onSuccess(isAuthenticated);
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
        ResponseCookie expiredAccessToken = ResponseCookie.from(ACCESS_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie expiredRefreshToken = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/auth")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, expiredAccessToken.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, expiredRefreshToken.toString());
    }
}
