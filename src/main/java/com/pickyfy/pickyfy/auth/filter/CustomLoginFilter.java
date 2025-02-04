package com.pickyfy.pickyfy.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pickyfy.pickyfy.common.Constant;
import com.pickyfy.pickyfy.common.util.JwtUtil;
import com.pickyfy.pickyfy.common.util.RedisUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

@RequiredArgsConstructor
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

    private static final String CAN_NOT_READ_BODY = "인증 요청 본문을 읽을 수 없습니다.";

    private final AuthenticationManager authenticationManager;
    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            Map<String, String> credentials = objectMapper.readValue(request.getInputStream(), Map.class);
            String username = credentials.get("principal");
            String password = credentials.get("password");

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);

            return authenticationManager.authenticate(token);
        } catch (IOException e) {
            throw new AuthenticationServiceException(CAN_NOT_READ_BODY);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication){

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String principal = userDetails.getUsername();
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);

        String accessToken = jwtUtil.createAccessToken(principal, role);
        String refreshToken = jwtUtil.createRefreshToken(principal, role);

        redisUtil.setDataExpire("refresh:" + jwtUtil.getPrincipal(refreshToken), refreshToken, Constant.REFRESH_TOKEN_EXPIRATION_TIME);
        response.setHeader("Authorization", "Bearer " + accessToken);
        ResponseCookie cookie = createCookie("refreshToken", refreshToken, Constant.REFRESH_TOKEN_EXPIRATION_TIME);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        getFailureHandler().onAuthenticationFailure(request, response, exception);
    }

    public ResponseCookie createCookie(String name, String value, long maxAge) {
        return ResponseCookie.from(name, value)
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofMillis(maxAge).getSeconds())
                .build();
    }
}