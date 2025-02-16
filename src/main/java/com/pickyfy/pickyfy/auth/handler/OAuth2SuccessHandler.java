package com.pickyfy.pickyfy.auth.handler;

import com.pickyfy.pickyfy.common.Constant;
import com.pickyfy.pickyfy.common.util.JwtUtil;
import com.pickyfy.pickyfy.common.util.RedisUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final String REDIRECT_URL = "https://team-pickify.store/";

    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        String accessToken = jwtUtil.createAccessToken(email, "USER");
        String refreshToken = jwtUtil.createRefreshToken(email, "USER");

        redisUtil.setDataExpire("refresh:" + email, refreshToken, Constant.REFRESH_TOKEN_EXPIRATION_TIME);

        ResponseCookie accessCookie = createCookie("accessToken", accessToken, Constant.ACCESS_TOKEN_EXPIRATION_TIME, "/");
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        ResponseCookie refreshCookie = createCookie("refreshToken", refreshToken, Constant.REFRESH_TOKEN_EXPIRATION_TIME, "/auth");
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        response.sendRedirect(REDIRECT_URL);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private ResponseCookie createCookie(String name, String token, long expirationTime, String path) {
        return ResponseCookie.from(name, token)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path(path)
                .maxAge(Duration.ofMillis(expirationTime).getSeconds())
                .build();
    }
}