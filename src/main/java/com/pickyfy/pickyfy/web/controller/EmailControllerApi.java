package com.pickyfy.pickyfy.web.controller;

import com.pickyfy.pickyfy.apiPayload.ApiResponse;
import com.pickyfy.pickyfy.web.dto.request.EmailVerificationSendRequest;
import com.pickyfy.pickyfy.web.dto.request.EmailVerificationVerifyRequest;
import com.pickyfy.pickyfy.web.dto.response.EmailVerificationSendResponse;
import com.pickyfy.pickyfy.web.dto.response.EmailVerificationVerifyResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "이메일 인증")
public interface EmailControllerApi {

    @Operation(summary = "이메일 인증코드 전송 API", description = "이메일 인증코드 전송 API입니다. 이메일을 입력해주세요 ")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @PostMapping("/send-code")
    ApiResponse<EmailVerificationSendResponse> sendAuthCode(@RequestBody EmailVerificationSendRequest emailVerificationSendRequest);

    @Operation(summary = "이메일 인증코드 검증 API", description = "이메일 인증코드 검증 API입니다. 이메일과 인증코드를 입력해주세요 ")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @PostMapping("/verify-code")
    ApiResponse<EmailVerificationVerifyResponse> verifyAuthCode(@RequestBody EmailVerificationVerifyRequest emailVerificationVerifyRequest);
}
