package com.pickyfy.pickyfy.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final String LOGIN_FAILED = "로그인 실패.";
    private static final String CONTENT_TYPE = "application/json;charset=UTF-8";

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(CONTENT_TYPE);

        String errorMessage;
        int errorCode;

        if (exception instanceof UsernameNotFoundException) {
            errorMessage = exception.getMessage();
            errorCode = 4001;
        } else if (exception instanceof BadCredentialsException) {
            errorMessage = exception.getMessage();
            errorCode = 4002;
        } else if (exception instanceof AuthenticationServiceException){
            errorMessage = exception.getMessage();
            errorCode = 4003;
        } else {
            errorMessage = LOGIN_FAILED;
            errorCode = 4004;
        }

        String jsonResponse = String.format("{\"error\": \"%s\", \"status\": %d, \"errorCode\": %d}",
                errorMessage, HttpServletResponse.SC_UNAUTHORIZED, errorCode);

        response.getWriter().write(jsonResponse);
    }
}