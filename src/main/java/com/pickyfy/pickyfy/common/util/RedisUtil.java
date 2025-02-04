package com.pickyfy.pickyfy.common.util;

import com.pickyfy.pickyfy.apiPayload.code.status.ErrorStatus;
import com.pickyfy.pickyfy.exception.handler.ExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisUtil {

    private final StringRedisTemplate stringRedisTemplate;

    public void setDataExpire(String key, String value, long duration) {
        try {
            ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
            Duration expireDuration = Duration.ofSeconds(duration);
            valueOperations.set(key, value, expireDuration);
        }catch(DataAccessException e){
            throw new ExceptionHandler(ErrorStatus._INTERNAL_SERVER_ERROR);
        }
    }

    public String getData(String key){
        try {
            ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
            return valueOperations.get(key);
        } catch(DataAccessException e){
            throw new ExceptionHandler(ErrorStatus.KEY_NOT_FOUNT);
        }
    }

    public void deleteRefreshToken(String redisKey) {
        try {
            stringRedisTemplate.delete(redisKey);
        } catch (DataAccessException e) {
            throw new ExceptionHandler(ErrorStatus._INTERNAL_SERVER_ERROR);
        }
    }
}