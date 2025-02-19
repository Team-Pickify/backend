package com.pickyfy.pickyfy.web.controller;

import com.pickyfy.pickyfy.web.apiResponse.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "인증")
public interface AuthControllerApi {

    @Operation(summary = "로그아웃 API", description = """
            - 로그아웃 API입니다.
            - 쿠키로 저장된 refresh 토큰이 자동으로 전송됩니다.""")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @PostMapping("/logout")
    ApiResponse<Void> logout(@CookieValue(value = "refreshToken", required = false) String refreshToken, HttpServletResponse response);

    @Operation(summary = "토큰 재발급 API", description = """
        - 액세스 토큰을 재발급하는 API입니다.
        - 쿠키로 저장된 refresh 토큰이 자동으로 전송됩니다.""")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @PostMapping("/reissue")
    ApiResponse<Void> reIssue(@CookieValue(value = "refreshToken", required = false) String refreshToken, HttpServletResponse response);

    @Operation(summary = "로그인 상태 검증 API", description = """
        - 로그인 상태를 검증하는 API입니다.
        - 쿠키로 저장된 엑세스토큰을 기반으로 검증합니다.""")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @PostMapping("/me")
    ApiResponse<Boolean> isAuthenticated(@CookieValue(value = "accessToken", required = false) String accessToken);
}
