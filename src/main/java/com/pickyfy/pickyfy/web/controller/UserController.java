package com.pickyfy.pickyfy.web.controller;

import com.pickyfy.pickyfy.apiPayload.ApiResponse;
import com.pickyfy.pickyfy.auth.custom.CustomUserDetails;
import com.pickyfy.pickyfy.common.util.JwtUtil;
import com.pickyfy.pickyfy.common.util.RedisUtil;
import com.pickyfy.pickyfy.web.dto.request.UserCreateRequest;
import com.pickyfy.pickyfy.web.dto.request.UserUpdateRequest;
import com.pickyfy.pickyfy.web.dto.response.UserCreateResponse;
import com.pickyfy.pickyfy.service.UserService;
import com.pickyfy.pickyfy.web.dto.response.UserInfoResponse;
import com.pickyfy.pickyfy.web.dto.response.UserUpdateResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController implements UserControllerApi{

    private final UserService userService;
    private final RedisUtil redisUtil;
    private final JwtUtil jwtUtil;

    @Override
    public ApiResponse<UserCreateResponse> signUp(@Valid UserCreateRequest userCreateRequest) {
        UserCreateResponse userCreateResponse = userService.signUp(userCreateRequest);
        return ApiResponse.onSuccess(userCreateResponse);
    }

    @GetMapping
    public ApiResponse<UserInfoResponse> getUserInfo(){
        CustomUserDetails userDetails = getUserDetails();
        UserInfoResponse userInfoResponse = new UserInfoResponse(userDetails.getUsername(), userDetails.getProfileImage());
        return ApiResponse.onSuccess(userInfoResponse);
    }

    @PatchMapping("/update")
    public ApiResponse<UserUpdateResponse> update(@Valid @RequestBody UserUpdateRequest userUpdateRequest){
        CustomUserDetails userDetails = getUserDetails();
        UserUpdateResponse userUpdateResponse = userService.update(userUpdateRequest, userDetails.getEmail());
        return ApiResponse.onSuccess(userUpdateResponse);
    }

    //TODO: 책임분리
    @PatchMapping("/logout")
    public ApiResponse<String> logout(@Valid @RequestBody String accessToken){

        // 토큰 검증은 필터에서?

        // 레디스에 유저 정보에 따라 저장된 리프레시 토큰이 있는지 확인하고 삭제
        CustomUserDetails userDetails = getUserDetails();
        String redisKey = "email:" + userDetails.getEmail();
        redisUtil.deleteRefreshToken(redisKey);

        // 해당 엑세스 토큰 가져와서 블랙 리스트에 저장하기
        Long expiration = jwtUtil.getExpirationDate(accessToken);
        redisUtil.blacklistAccessToken(accessToken, expiration);

        return ApiResponse.onSuccess("로그아웃에 성공했습니다.");
    }


    //TODO: 비밀번호 재설정


    //TODO: 회원탈퇴



    private CustomUserDetails getUserDetails(){
        return (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
