package com.pickyfy.pickyfy.web.controller;

import com.pickyfy.pickyfy.apiPayload.ApiResponse;
import com.pickyfy.pickyfy.service.UserService;
import com.pickyfy.pickyfy.web.dto.request.EmailVerificationSendRequest;
import com.pickyfy.pickyfy.web.dto.request.PasswordResetRequest;
import com.pickyfy.pickyfy.web.dto.request.UserCreateRequest;
import com.pickyfy.pickyfy.web.dto.request.UserUpdateRequest;
import com.pickyfy.pickyfy.web.dto.response.UserCreateResponse;
import com.pickyfy.pickyfy.web.dto.response.UserInfoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController implements UserControllerApi{

    private final UserService userService;

    public ApiResponse<UserCreateResponse> signUp(@Valid UserCreateRequest request) {
        UserCreateResponse response = userService.signUp(request);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/getInfo")
    public ApiResponse<UserInfoResponse> getUserInfo(@RequestHeader("Authorization") String header){
        String token = extractToken(header);
        UserInfoResponse response = userService.getUser(token);
        return ApiResponse.onSuccess(response);
    }

    @PatchMapping("/update")
    public ApiResponse<Long> updateUser(
            @RequestHeader("Authorization") String header,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @Valid @RequestBody UserUpdateRequest request
    ){
        String token = extractToken(header);
        Long userId = userService.updateUser(token, request, image);
        return ApiResponse.onSuccess(userId);
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
        return ApiResponse.onSuccess("회원 탈퇴에 성공했습니다.");
    }

    @PostMapping("/verify-by-email")
    public ApiResponse<String> verifyByEmail(@Valid @RequestBody EmailVerificationSendRequest request){
        userService.verifyByEmail(request.email());
        return ApiResponse.onSuccess("이메일 인증에 성공했습니다.");
    }

    @PatchMapping("/reset-password")
    public ApiResponse<String> resetPassword(@Valid @RequestBody PasswordResetRequest request){
        userService.resetPassword(request);
        return ApiResponse.onSuccess("비밀번호 변경에 성공했습니다.");
    }

    private String extractToken(String authorizationHeader){
        return authorizationHeader.replace("Bearer ", "");
    }
}
