package com.pickyfy.pickyfy.auth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        String message = "인증 정보가 없습니다. 로그인 후 다시 시도하세요";

        if(request.getAttribute("errorMessage") != null){
            message = request.getAttribute("errorMessage").toString();
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format("""
                {
                    "isSuccess": false,
                    "code": "401",
                    "message": "%s"
                }
                """, message));
    }
}