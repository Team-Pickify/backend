package com.pickyfy.pickyfy.service;

import com.pickyfy.pickyfy.apiPayload.code.status.ErrorStatus;
import com.pickyfy.pickyfy.common.Constant;
import com.pickyfy.pickyfy.common.util.JwtUtil;
import com.pickyfy.pickyfy.common.util.RedisUtil;
import com.pickyfy.pickyfy.domain.*;
import com.pickyfy.pickyfy.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import com.pickyfy.pickyfy.domain.Place;
import com.pickyfy.pickyfy.domain.PlaceImage;
import com.pickyfy.pickyfy.web.dto.request.PlaceCreateRequest;
import jakarta.persistence.EntityExistsException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService{

    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final AdminRepository adminRepository;
    @Override
    @Transactional
    public void logout(String accessToken){
        String adminName = jwtUtil.getPrincipal(accessToken);

        Long expiration = jwtUtil.getExpirationDate(accessToken);
        redisUtil.blacklistAccessToken(accessToken, expiration);

        String redisKey = Constant.REDIS_KEY_PREFIX + adminName;
        redisUtil.deleteRefreshToken(redisKey);
    }

}

