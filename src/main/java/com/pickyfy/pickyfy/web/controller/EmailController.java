package com.pickyfy.pickyfy.web.controller;

import com.pickyfy.pickyfy.web.apiResponse.common.ApiResponse;
import com.pickyfy.pickyfy.web.dto.request.EmailVerificationSendRequest;
import com.pickyfy.pickyfy.web.dto.request.EmailVerificationVerifyRequest;
import com.pickyfy.pickyfy.web.dto.response.EmailVerificationSendResponse;
import com.pickyfy.pickyfy.web.dto.response.EmailVerificationVerifyResponse;
import com.pickyfy.pickyfy.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email-auth")
public class EmailController implements EmailControllerApi {

    private final EmailService emailService;

    @Override
    public ApiResponse<EmailVerificationSendResponse> sendAuthCode(
            @Valid @RequestBody EmailVerificationSendRequest emailVerificationSendRequest
    ){
        EmailVerificationSendResponse emailVerificationSendResponse = emailService.sendAuthCode(emailVerificationSendRequest);
        return ApiResponse.onSuccess(emailVerificationSendResponse);
    }

    @Override
    public ApiResponse<EmailVerificationVerifyResponse> verifyAuthCode(
            @Valid @RequestBody EmailVerificationVerifyRequest emailVerificationVerifyRequest
    ){
        EmailVerificationVerifyResponse emailVerificationVerifyResponse = emailService.verifyAuthCode(emailVerificationVerifyRequest);
        return ApiResponse.onSuccess(emailVerificationVerifyResponse);
    }
}