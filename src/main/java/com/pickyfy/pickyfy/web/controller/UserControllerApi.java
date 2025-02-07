package com.pickyfy.pickyfy.web.controller;

import com.pickyfy.pickyfy.web.apiResponse.common.ApiResponse;
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

@Tag(name = "사용자")
public interface UserControllerApi {

    @Operation(summary = "회원가입 API", description = "- 닉네임, 비밀번호, 이메일, 이메일 토큰을 입력해주세요." +
            "\n- 비밀번호는 영문, 숫자, 특수문자를 포함하여 최소 8자 이상이어야 합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @PostMapping("/signup")
    ApiResponse<UserCreateResponse> signUp(@RequestBody UserCreateRequest userCreateRequest);

    @Operation(summary = "유저조회 API", description = "- 유저의 닉네임 및 프로필 사진이 조회됩니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @GetMapping("/getInfo")
    ApiResponse<UserInfoResponse> getUserInfo();

    @Operation(summary = "유저정보 수정 API", description = "- 변경하실 유저의 프로필 이미지 혹은 닉네임을 입력해주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @PatchMapping("/update")
    ApiResponse<Long> updateUser(@RequestHeader("Authorization") String header,
                                 @Valid @ModelAttribute UserUpdateRequest request);

    @Operation(summary = "회원탈퇴 API", description = "회원탈퇴 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @DeleteMapping("/signOut")
    ApiResponse<String> signOut();

    @Operation(summary = "비밀번호 재설정 시 유저검증 API", description =
            """
                    - 이메일을 입력해주세요.\
                    
                    - 비밀번호 재설정을 위해 해당 이메일을 가진 유저 정보가 존재하는지 검증합니다.\

                    - 비밀번호 재설정 단계\

                    \t 1. /users/verify-by-email 유저 이메일 검증\

                    \t 2. /email-auth/send-auth-code 1번에서 받은 이메일로 인증코드 전송\

                    \t 3. /email-auth/verifyAuth-code 인증번호 검증\

                    \t 4. /users/reset-password 비밀번호 재설정""")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @PostMapping("/verify-by-email")
    ApiResponse<String> verifyByEmail(@Valid @RequestBody EmailVerificationSendRequest request);

    @Operation(summary = "비밀번호 재설정 API", description = "- 이메일, 새로운 비밀번호, 이메일 토큰을 입력해주세요.\n- 비밀번호는 8자 이상의 영문, 숫자, 특수문자를 조합하여 입력해주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @PostMapping("/reset-password")
    ApiResponse<String> resetPassword(@Valid @RequestBody PasswordResetRequest request);
}
