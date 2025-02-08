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
            - Swagger에서는 저장된 refresh 토큰이 자동으로 전송됩니다.
            - 완료 후 클라이언트측에서 Authorization header의 AccessToken을 제거해주세요.""")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @PostMapping("/logout")
    ApiResponse<String> logout(@CookieValue(value = "refreshToken", required = false) String refreshToken, HttpServletResponse response);

    @Operation(summary = "토큰 재발급 API", description = """
        - 액세스 토큰을 재발급하는 API입니다.
        - 요청 시 Authorization 헤더를 포함 x.
        - Swagger에서는 저장된 refresh 토큰이 자동으로 전송됩니다.""")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @PostMapping("/reissue")
    ApiResponse<String> reIssue(@CookieValue(value = "refreshToken", required = false) String refreshToken, HttpServletResponse response);
}
