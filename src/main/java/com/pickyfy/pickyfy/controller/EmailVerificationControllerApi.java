package com.pickyfy.pickyfy.controller;

import com.pickyfy.pickyfy.apiPayload.ApiResponse;
import com.pickyfy.pickyfy.dto.request.EmailVerificationRequest;
import com.pickyfy.pickyfy.dto.response.EmailVerificationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.RequestBody;

public interface EmailVerificationControllerApi {

    @Operation(summary = "이메일 인증코드 전송 API", description = "이메일 인증코드 전송 API입니다. 이메일을 입력해주세요 ")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    ApiResponse<EmailVerificationResponse> sendVerificationCode(@RequestBody EmailVerificationRequest emailVerificationRequest);
}
