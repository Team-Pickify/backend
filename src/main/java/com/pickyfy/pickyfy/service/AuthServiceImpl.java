package com.pickyfy.pickyfy.service;

import com.pickyfy.pickyfy.common.Constant;
import com.pickyfy.pickyfy.common.util.JwtUtil;
import com.pickyfy.pickyfy.common.util.RedisUtil;
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
}
