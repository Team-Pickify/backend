package com.pickyfy.pickyfy.web.controller;

import com.pickyfy.pickyfy.apiPayload.ApiResponse;
import com.pickyfy.pickyfy.auth.custom.CustomUserDetails;
import com.pickyfy.pickyfy.common.util.JwtUtil;
import com.pickyfy.pickyfy.common.util.RedisUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final RedisUtil redisUtil;
    private final JwtUtil jwtUtil;

    @PatchMapping("/logout")
    public ApiResponse<String> logout(@Valid @RequestBody String accessToken){

        // 토큰 검증은 필터에서?

        //TODO: 추후 레디스 설정에 따라 수정(key 등)
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String redisKey = "name:" + userDetails.getEmail();
        redisUtil.deleteRefreshToken(redisKey);

        // 해당 엑세스 토큰 가져와서 블랙 리스트에 저장하기
        Long expiration = jwtUtil.getExpirationDate(accessToken);
        redisUtil.blacklistAccessToken(accessToken, expiration);

        return ApiResponse.onSuccess("로그아웃에 성공했습니다.");
    }

}
