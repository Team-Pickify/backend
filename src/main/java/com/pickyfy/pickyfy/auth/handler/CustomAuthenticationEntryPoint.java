package com.pickyfy.pickyfy.auth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Map;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        String defaultMessage = "인증 정보가 없습니다. 로그인 후 다시 시도하세요";
        String defaultCode = "401";

        Map<String, String> errorCodeMapping = Map.of(
                "유효하지 않은 토큰입니다.", "4012",
                "토큰 만료", "4013"
        );
        String message = (request.getAttribute("errorMessage") != null)
                ? request.getAttribute("errorMessage").toString()
                : defaultMessage;

        String code = errorCodeMapping.getOrDefault(message, defaultCode);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format("""
                {
                    "isSuccess": false,
                    "code": "%s",
                    "message": "%s"
                }
                """, code, message));
    }
}