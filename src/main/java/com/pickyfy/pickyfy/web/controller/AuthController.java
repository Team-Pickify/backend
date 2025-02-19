package com.pickyfy.pickyfy.web.controller;

import com.pickyfy.pickyfy.common.Constant;
import com.pickyfy.pickyfy.exception.InvalidRefreshTokenException;
import com.pickyfy.pickyfy.service.AuthService;
import com.pickyfy.pickyfy.web.apiResponse.common.ApiResponse;
import com.pickyfy.pickyfy.web.apiResponse.error.ErrorStatus;
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
            throw new InvalidRefreshTokenException(ErrorStatus.TOKEN_INVALID);
        }
        AuthResponse authResponse = authService.reIssue(refreshToken); // 서비스 메서드 호출
        response.setHeader("Authorization", "Bearer " + authResponse.accessToken());
        createCookie(response, authResponse);
        return ApiResponse.onSuccess(SuccessStatus.REISSUE_TOKEN_SUCCESS, null);
    }

    @Override
    public ApiResponse<Boolean> isAuthenticated(
            @Parameter(hidden = true) @CookieValue(name = "accessToken", required = false) String accessToken
    ) {
        boolean isAuthenticated = authService.isAuthenticated(accessToken);
        return ApiResponse.onSuccess(isAuthenticated);
    }

    private void createCookie(HttpServletResponse response, AuthResponse token) {
        ResponseCookie expiredAccessToken = ResponseCookie.from(ACCESS_TOKEN_COOKIE_NAME, token.accessToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(Duration.ofMillis(Constant.ACCESS_TOKEN_EXPIRATION_TIME).getSeconds())
                .build();

        ResponseCookie expiredRefreshToken = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, token.refreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/auth")
                .maxAge(Duration.ofMillis(Constant.REFRESH_TOKEN_EXPIRATION_TIME).getSeconds())
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, expiredAccessToken.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, expiredRefreshToken.toString());
    }

    private void clearCookie(HttpServletResponse response) {
        ResponseCookie expiredAccessToken = ResponseCookie.from(ACCESS_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie expiredRefreshToken = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/auth")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, expiredAccessToken.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, expiredRefreshToken.toString());
    }
}
