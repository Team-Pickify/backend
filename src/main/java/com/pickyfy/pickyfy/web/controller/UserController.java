package com.pickyfy.pickyfy.web.controller;

import com.pickyfy.pickyfy.apiPayload.ApiResponse;
import com.pickyfy.pickyfy.service.UserService;
import com.pickyfy.pickyfy.web.dto.request.UserCreateRequest;
import com.pickyfy.pickyfy.web.dto.request.UserUpdateRequest;
import com.pickyfy.pickyfy.web.dto.response.UserCreateResponse;
import com.pickyfy.pickyfy.web.dto.response.UserInfoResponse;
import com.pickyfy.pickyfy.web.dto.response.UserUpdateResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController implements UserControllerApi{

    private final UserService userService;

    @Override
    public ApiResponse<UserCreateResponse> signUp(@Valid UserCreateRequest request) {
        UserCreateResponse response = userService.signUp(request);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping
    public ApiResponse<UserInfoResponse> getUserInfo(@RequestHeader("Authorization") String header){
        String token = extractToken(header);
        UserInfoResponse response = userService.getUser(token);
        return ApiResponse.onSuccess(response);
    }

    @PatchMapping("/update")
    public ApiResponse<UserUpdateResponse> updateUser(
            @RequestHeader("Authorization") String header,
            @Valid @RequestBody UserUpdateRequest request
    ){
        String token = extractToken(header);
        UserUpdateResponse response = userService.updateUser(token, request);
        return ApiResponse.onSuccess(response);
    }

    @PatchMapping("/logout")
    public ApiResponse<String> logout(@RequestHeader("Authorization") String header){
        String token = extractToken(header);
        userService.logout(token);
        return ApiResponse.onSuccess("로그아웃에 성공했습니다.");
    }

    @DeleteMapping("/signOut")
    public ApiResponse<String> signOut(@RequestHeader("Authorization") String header){
        String token = extractToken(header);
        userService.signOut(token);
        return ApiResponse.onSuccess("회원 탈퇴 성공");
    }

    //TODO: 비밀번호 재설정


    private String extractToken(String authorizationHeader){
        return authorizationHeader.replace("Bearer ", "");
    }
}
