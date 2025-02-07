package com.pickyfy.pickyfy.web.controller;

import com.pickyfy.pickyfy.common.util.TokenExtractor;
import com.pickyfy.pickyfy.web.apiResponse.common.ApiResponse;
import com.pickyfy.pickyfy.service.UserService;
import com.pickyfy.pickyfy.web.dto.request.EmailVerificationSendRequest;
import com.pickyfy.pickyfy.web.dto.request.PasswordResetRequest;
import com.pickyfy.pickyfy.web.dto.request.UserCreateRequest;
import com.pickyfy.pickyfy.web.dto.request.UserUpdateRequest;
import com.pickyfy.pickyfy.web.dto.response.UserCreateResponse;
import com.pickyfy.pickyfy.web.dto.response.UserInfoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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
        String token = TokenExtractor.extract(header);
        UserInfoResponse response = userService.getUser(token);
        return ApiResponse.onSuccess(response);
    }

    @PatchMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Long> updateUser(
            @RequestHeader("Authorization") String header,
            @Valid @ModelAttribute UserUpdateRequest request
    ){
        String token = TokenExtractor.extract(header);
        Long userId = userService.updateUser(token, request);
        return ApiResponse.onSuccess(userId);
    }

    @DeleteMapping("/signOut")
    public ApiResponse<String> signOut(@RequestHeader("Authorization") String header){
        String token = TokenExtractor.extract(header);
        userService.signOut(token);
        return ApiResponse.onSuccess("회원 탈퇴에 성공했습니다.");
    }

    @PostMapping("/verify-by-email")
    public ApiResponse<String> verifyByEmail(@Valid @RequestBody EmailVerificationSendRequest request){
        userService.verifyByEmail(request.email());
        return ApiResponse.onSuccess("존재하는 유저 정보입니다.");
    }

    @PatchMapping("/reset-password")
    public ApiResponse<String> resetPassword(@Valid @RequestBody PasswordResetRequest request){
        userService.resetPassword(request);
        return ApiResponse.onSuccess("비밀번호 변경에 성공했습니다.");
    }
}
