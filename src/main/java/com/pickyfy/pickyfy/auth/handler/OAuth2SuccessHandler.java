package com.pickyfy.pickyfy.auth.handler;

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

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final static long REFRESH_TOKEN_EXPIRATION_TIME = 7 * 24 * 60 * 60;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        String accessToken = jwtUtil.createAccessToken(email, "USER");
        String refreshToken = jwtUtil.createRefreshToken(email, "USER");

        redisUtil.setDataExpire("refresh:" + email, refreshToken, REFRESH_TOKEN_EXPIRATION_TIME);
        response.setHeader("Authorization", "Bearer " + accessToken);

        ResponseCookie cookie = createCookie("refreshToken", refreshToken, REFRESH_TOKEN_EXPIRATION_TIME);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        String redirect = getRedirectUrl(request);
        response.sendRedirect(redirect);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public String getRedirectUrl(HttpServletRequest request) {
        return request.getSession().getAttribute("redirect").toString();
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