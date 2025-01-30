package com.pickyfy.pickyfy.service;

import com.pickyfy.pickyfy.common.util.JwtUtil;
import com.pickyfy.pickyfy.common.util.RedisUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{

    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    private static final String REDIS_KEY_PREFIX = "refresh:";

    @Override
    @Transactional
    public void logout(String accessToken){
        String adminName = jwtUtil.getPrincipal(accessToken);

        Long expiration = jwtUtil.getExpirationDate(accessToken);
        redisUtil.blacklistAccessToken(accessToken, expiration);

        String redisKey = REDIS_KEY_PREFIX + adminName;
        redisUtil.deleteRefreshToken(redisKey);
    }
}
