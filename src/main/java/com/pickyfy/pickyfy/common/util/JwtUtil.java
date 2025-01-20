package com.pickyfy.pickyfy.common.util;

import com.pickyfy.pickyfy.dto.CustomUserInfoDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private static final String EMAIL = "email";

    private final Key key;
    private final long accessTokenExpTime;

    public JwtUtil(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration_time}") long accessTokenExpTime
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpTime = accessTokenExpTime;
    }

    public String createAccessToken(CustomUserInfoDto customUserInfoDto) {
        return createToken(customUserInfoDto, accessTokenExpTime);
    }

    private String createToken(CustomUserInfoDto customUserInfoDto, long expireTime) {

        String email = customUserInfoDto.getEmail();
        Date now = new Date();

        Date tokenValidity = new Date(now.getTime() + expireTime * 1000);

        return Jwts.builder()
                .claim(EMAIL, email)
                .issuedAt(now)
                .expiration(tokenValidity)
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 서명 혹은 JWT 형식 오류", e);
        } catch (ExpiredJwtException e) {
            log.info("토큰 만료", e);
        } catch (UnsupportedJwtException e) {
            log.info("지원하지 않는 서명 알고리즘", e);
        } catch (IllegalArgumentException e) {
            log.info("올바르지 않은 값 입력(토큰 문자열 null)", e);
        }
        return false;
    }

    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(accessToken).getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public String getUserEmail(String token) {
        return parseClaims(token).get(EMAIL, String.class);
    }
}