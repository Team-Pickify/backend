package com.pickyfy.pickyfy.web.controller;

import com.pickyfy.pickyfy.auth.details.CustomUserDetails;
import com.pickyfy.pickyfy.exception.ExceptionHandler;
import com.pickyfy.pickyfy.web.apiResponse.common.ApiResponse;
import com.pickyfy.pickyfy.service.UserService;
import com.pickyfy.pickyfy.web.apiResponse.error.ErrorStatus;
import com.pickyfy.pickyfy.web.apiResponse.success.SuccessStatus;
import com.pickyfy.pickyfy.web.dto.request.EmailVerificationSendRequest;
import com.pickyfy.pickyfy.web.dto.request.PasswordResetRequest;
import com.pickyfy.pickyfy.web.dto.request.UserCreateRequest;
import com.pickyfy.pickyfy.web.dto.request.UserUpdateRequest;
import com.pickyfy.pickyfy.web.dto.response.UserCreateResponse;
import com.pickyfy.pickyfy.web.dto.response.UserInfoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController implements UserControllerApi{

    private final UserService userService;

    public ApiResponse<UserCreateResponse> signUp(@Valid UserCreateRequest request) {
        UserCreateResponse response = userService.signUp(request);
        return ApiResponse.onSuccess(SuccessStatus.SIGN_IN_SUCCESS, response);
    }

    @GetMapping("/getInfo")
    public ApiResponse<UserInfoResponse> getUserInfo(){
        UserInfoResponse response = userService.getUser(getUserEmail());
        return ApiResponse.onSuccess(SuccessStatus.USER_INFO_RETRIEVED, response);
    }

    @PatchMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Long> updateUser(@Valid @ModelAttribute UserUpdateRequest request) {
        Long userId = userService.updateUser(getUserEmail(), request);
        return ApiResponse.onSuccess(SuccessStatus.USER_EDIT_SUCCESS, userId);
    }

    @DeleteMapping("/signOut")
    public ApiResponse<Void> signOut(){
        userService.signOut(getUserEmail());
        return ApiResponse.onSuccess(SuccessStatus.USER_SING_OUT_SUCCESS,null);
    }

    @PostMapping("/verify-by-email")
    public ApiResponse<Void> verifyByEmail(@Valid @RequestBody EmailVerificationSendRequest request){
        userService.verifyByEmail(request.email());
        return ApiResponse.onSuccess(SuccessStatus.EMAIL_VERIFIED,null);
    }

    @PatchMapping("/reset-password")
    public ApiResponse<String> resetPassword(@Valid @RequestBody PasswordResetRequest request){
        userService.resetPassword(request);
        return ApiResponse.onSuccess("비밀번호 변경에 성공했습니다.");
    }

    private String getUserEmail(){
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(CustomUserDetails.class::isInstance)
                .map(CustomUserDetails.class::cast)
                .map(CustomUserDetails::getUsername)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));
    }
}
