package com.pickyfy.pickyfy.common.util;

import com.pickyfy.pickyfy.web.apiResponse.error.ErrorStatus;
import com.pickyfy.pickyfy.common.Constant;
import com.pickyfy.pickyfy.exception.ExceptionHandler;
import com.pickyfy.pickyfy.web.dto.response.TokenValidationResult;
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
import java.util.HashMap;
import java.util.Map;

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

    public void validateToken(String token) {
        try {
            Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(token);
            TokenValidationResult.success("검증 완료.");
        } catch (Exception e) {
            handleJwtException(e, true);
        }
    }

    public TokenValidationResult validateTokenWithoutException(String token) {
        try {
            Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(token);
            return TokenValidationResult.success("검증 완료.");
        } catch (Exception e) {
            return handleJwtException(e, false);
        }
    }

    private TokenValidationResult handleJwtException(Exception e, boolean throwException) {
        Map<Class<? extends Exception>, String> errorMessages = new HashMap<>();
        errorMessages.put(SecurityException.class, "유효하지 않은 토큰입니다.");
        errorMessages.put(MalformedJwtException.class, "유효하지 않은 토큰입니다.");
        errorMessages.put(UnsupportedJwtException.class, "유효하지 않은 토큰입니다.");
        errorMessages.put(IllegalArgumentException.class, "유효하지 않은 토큰입니다.");
        errorMessages.put(ExpiredJwtException.class, "토큰 만료");

        String message = errorMessages.getOrDefault(e.getClass(), "알 수 없는 JWT 오류");
        log.info(message, e);

        if (throwException) {
            throw new ExceptionHandler(
                    e instanceof ExpiredJwtException ? ErrorStatus.TOKEN_EXPIRATION : ErrorStatus.TOKEN_INVALID
            );
        }

        return TokenValidationResult.failure(message);
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