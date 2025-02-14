package com.pickyfy.pickyfy.service;

import com.pickyfy.pickyfy.common.Constant;
import com.pickyfy.pickyfy.common.util.JwtUtil;
import com.pickyfy.pickyfy.common.util.RedisUtil;
import com.pickyfy.pickyfy.exception.handler.ExceptionHandler;
import com.pickyfy.pickyfy.web.apiResponse.error.ErrorStatus;
import com.pickyfy.pickyfy.web.dto.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final RedisUtil redisUtil;
    private final JwtUtil jwtUtil;

    @Override
    public void logout(String refreshToken){
        redisUtil.deleteRefreshToken(Constant.REDIS_KEY_PREFIX + jwtUtil.getPrincipal(refreshToken));
    }

    @Override
    public AuthResponse reIssue(String token){
        validateRefreshToken(token);
        String principal = jwtUtil.getPrincipal(token);
        String accessToken = jwtUtil.createAccessToken(principal, jwtUtil.getRole(token));
        String refreshToken = jwtUtil.createRefreshToken(principal, jwtUtil.getRole(token));

        redisUtil.setDataExpire("refresh:" + jwtUtil.getPrincipal(refreshToken), refreshToken, Constant.REFRESH_TOKEN_EXPIRATION_TIME);
        return AuthResponse.from(accessToken, refreshToken);
    }

    @Override
    public boolean isAuthenticated(String accessToken) {
        return jwtUtil.validateToken(accessToken);
    }

    public void validateRefreshToken(String token){
        jwtUtil.validateToken(token);
        String getToken = redisUtil.getData("refresh:" + jwtUtil.getPrincipal(token));
        if(!token.equals(getToken)){
            throw new ExceptionHandler(ErrorStatus.TOKEN_INVALID);
        }
    }
}
