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
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final static long REFRESH_TOKEN_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        String email = authentication.getPrincipal().toString();

        // 1. Access Token 발급
        String accessToken = jwtUtil.createAccessToken(email);

        // 2. Refresh Token 발급
        String refreshToken = jwtUtil.createRefreshToken(email);

        // 3. Refresh Token을 Redis에 저장
        redisUtil.setDataExpire("refresh:" + email, refreshToken, REFRESH_TOKEN_EXPIRATION_TIME);

        // 4. Access Token은 Authorization 헤더에 추가
        response.setHeader("Authorization", "Bearer " + accessToken);

        // 5. Refresh Token은 쿠키에 추가
        ResponseCookie cookie = createCookie("refreshToken", refreshToken, REFRESH_TOKEN_EXPIRATION_TIME);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        String redirect = getRedirectUrl(request);
        response.sendRedirect(redirect);

    }

    /*
    redirectUrl 설정 메서드
     */
    public String getRedirectUrl(HttpServletRequest request) {
        return request.getSession().getAttribute("redirect").toString();
    }

    /*
    쿠키 생성 메서드
     */
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


//        Cookie cookie = new Cookie(name, value);
//        cookie.setHttpOnly(true); // JavaScript에서 접근 불가하도록 설정
//        cookie.setSecure(true); // HTTPS에서만 전송되도록 설정
//        cookie.setPath("/"); // 모든 경로에서 쿠키 접근 가능
//        cookie.setMaxAge((int) (maxAge / 1000)); // 만료 시간 설정 (초 단위)
//        return cookie;