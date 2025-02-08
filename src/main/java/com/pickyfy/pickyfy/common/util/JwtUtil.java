package com.pickyfy.pickyfy.common.util;

import com.pickyfy.pickyfy.web.apiResponse.error.ErrorStatus;
import com.pickyfy.pickyfy.common.Constant;
import com.pickyfy.pickyfy.exception.handler.ExceptionHandler;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    @Getter
    private enum Type{
        EMAIL("Email"),
        ACCESS("Access"),
        REFRESH("Refresh");

        private final String type;

        Type(String type) {
            this.type = type;
        }
    }

    private static final String PRINCIPAL = "principal";
    private static final String ROLE = "ROLE";

    private final Key key;

    public JwtUtil(
            @Value("${jwt.secret}") String secretKey
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(String principal, String role) {
        return createToken(principal, role, Constant.ACCESS_TOKEN_EXPIRATION_TIME, Type.ACCESS.getType());
    }

    public String createRefreshToken(String principal, String role){
        return createToken(principal, role, Constant.REFRESH_TOKEN_EXPIRATION_TIME, Type.REFRESH.getType());
    }

    public String createEmailToken(String principal) {
        return createToken(principal, "USER", Constant.EMAIL_TOKEN_EXPIRATION_TIME, Type.EMAIL.getType());
    }

    private String createToken(String principal, String role, long expireTime, String type) {

        Date now = new Date();

        Date tokenValidity = new Date(now.getTime() + expireTime);

        return Jwts.builder()
                .claim("tokenType", type)
                .claim(PRINCIPAL, principal)
                .claim(ROLE, role)
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
            throw new ExceptionHandler(ErrorStatus.TOKEN_EXPIRATION);
        } catch (UnsupportedJwtException e) {
            log.info("지원하지 않는 서명 알고리즘", e);
        } catch (IllegalArgumentException e) {
            log.info("올바르지 않은 값 입력(토큰 문자열 null)", e);
        }
        return false;
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            log.info("토큰 만료", e);
            throw new ExceptionHandler(ErrorStatus.TOKEN_INVALID);
        } catch (SecurityException e){
            log.info("위조된 토큰", e);
            throw new ExceptionHandler(ErrorStatus.TOKEN_INVALID);
        }
    }

    public String getPrincipal(String token) {
        return parseClaims(token).get(PRINCIPAL, String.class);
    }

    public String getRole(String token) {
        return parseClaims(token).get(ROLE, String.class);
    }

}