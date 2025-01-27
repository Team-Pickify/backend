package com.pickyfy.pickyfy.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pickyfy.pickyfy.common.util.JwtUtil;
import com.pickyfy.pickyfy.common.util.RedisUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000;

    private final AuthenticationManager authenticationManager;
    private final ObjectMapper objectMapper; //뭐지이게
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            Map<String, String> credentials = objectMapper.readValue(request.getInputStream(), Map.class);
            String email = credentials.get("email");
            String password = credentials.get("password");
            //obtainpassword왜 안먹지

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);

            return authenticationManager.authenticate(token); // 실제 인증 시도
        } catch (AuthenticationException e) {
            System.out.println("Authentication failed: " + e.getMessage());
            throw e; // 인증 실패 예외를 다시 던짐
        } catch (IOException e) {
            System.out.println("Error reading request: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication){

        String accessToken = jwtUtil.createAccessToken(authentication.getPrincipal().toString());
        String refreshToken = jwtUtil.createAccessToken(authentication.getPrincipal().toString());

        redisUtil.setDataExpire("refresh:" + jwtUtil.getUserEmail(refreshToken), refreshToken, REFRESH_TOKEN_EXPIRATION_TIME);

        response.setHeader("Authorization", "Bearer " + accessToken);

        ResponseCookie cookie = createCookie("refreshToken", refreshToken, REFRESH_TOKEN_EXPIRATION_TIME);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        System.out.println("성공");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception){
        System.out.println("실패");
    }

    public ResponseCookie createCookie(String name, String value, long maxAge) {
        return ResponseCookie.from(name, value)
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .path("/")
                .maxAge(maxAge)
                .build();
    }
}