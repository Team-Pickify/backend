package com.pickyfy.pickyfy.web.controller;

import com.pickyfy.pickyfy.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;

public interface AuthControllerApi {

    @Operation(summary = "로그아웃 API", description = """
            - 로그아웃 API입니다.
            - Cookie에 저장되어있는 refresh 토큰을 입력해주세요
            - 완료 후 클라이언트측에서 Authorization header의 AccessToken을 제거해주세요.""")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @PostMapping("/logout")
    ApiResponse<String> logout(@CookieValue(value = "refreshToken", required = false) String refreshToken, HttpServletResponse response);
}
