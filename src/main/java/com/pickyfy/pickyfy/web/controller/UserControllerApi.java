package com.pickyfy.pickyfy.web.controller;

import com.pickyfy.pickyfy.apiPayload.ApiResponse;
import com.pickyfy.pickyfy.web.dto.request.EmailVerificationSendRequest;
import com.pickyfy.pickyfy.web.dto.request.PasswordResetRequest;
import com.pickyfy.pickyfy.web.dto.request.UserCreateRequest;
import com.pickyfy.pickyfy.web.dto.request.UserUpdateRequest;
import com.pickyfy.pickyfy.web.dto.response.UserCreateResponse;
import com.pickyfy.pickyfy.web.dto.response.UserInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "사용자")
public interface UserControllerApi {

    @Operation(summary = "회원가입 API", description = "회원가입 API입니다." +
            "닉네임, 비밀번호, 이메일, 이메일 토큰을 입력해주세요." +
            "비밀번호는 영문, 숫자, 특수문자를 포함하여 최소 8자 이상이어야 합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @PostMapping("/signup")
    ApiResponse<UserCreateResponse> signUp(@RequestBody UserCreateRequest userCreateRequest);

    @Operation(summary = "유저조회 API", description = "유저 조회 API입니다. 닉네임, 프로필 사진이 조회됩니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @GetMapping("/getInfo")
    ApiResponse<UserInfoResponse> getUserInfo(@RequestHeader("Authorization") String header);

    @Operation(summary = "유저정보 수정 API", description = "유저정보 수정 API입니다. 변경하실 유저 정보를 입력해주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @PatchMapping("/update")
    ApiResponse<Long> updateUser(@RequestHeader("Authorization") String header,
                                               @RequestPart(value = "image", required = false) MultipartFile image,
                                               @Valid @RequestBody UserUpdateRequest request);

    @Operation(summary = "로그아웃 API", description = "로그아웃 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @PatchMapping("/logout")
    ApiResponse<String> logout(@RequestHeader("Authorization") String header);

    @Operation(summary = "회원탈퇴 API", description = "회원탈퇴 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @DeleteMapping("/signOut")
    ApiResponse<String> signOut(@RequestHeader("Authorization") String header);

    @Operation(summary = "유저검증 API", description = "유저 검증 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @PostMapping("/verify-by-email")
    ApiResponse<String> verifyByEmail(@Valid @RequestBody EmailVerificationSendRequest request);

    @Operation(summary = "비밀번호 재설정 API", description = "비밀번호 재설정 API입니다." +
            "새로운 비밀번호를 입력해주세요." +
            "비밀번호는 영문, 숫자, 특수문자를 포함하여 최소 8자 이상이어야 합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @PostMapping("/reset-password")
    ApiResponse<String> resetPassword(@Valid @RequestBody PasswordResetRequest request);
}
