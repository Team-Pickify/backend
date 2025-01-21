package com.pickyfy.pickyfy.controller;

import com.pickyfy.pickyfy.apiPayload.ApiResponse;
import com.pickyfy.pickyfy.dto.request.EmailVerificationSendRequest;
import com.pickyfy.pickyfy.dto.request.EmailVerificationVerifyRequest;
import com.pickyfy.pickyfy.dto.response.EmailVerificationSendResponse;
import com.pickyfy.pickyfy.dto.response.EmailVerificationVerifyResponse;
import com.pickyfy.pickyfy.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/emails")
public class EmailController implements EmailControllerApi {

    private final EmailService emailService;

    @PostMapping("/verification/send")
    public ApiResponse<EmailVerificationSendResponse> sendVerificationCode(
            @Valid @RequestBody EmailVerificationSendRequest emailVerificationSendRequest
    ){
        EmailVerificationSendResponse emailVerificationSendResponse = emailService.sendVerificationCode(emailVerificationSendRequest);
        return ApiResponse.onSuccess(emailVerificationSendResponse);
    }

    @PostMapping("/verification/verify")
    public ApiResponse<EmailVerificationVerifyResponse> verifyVerificationCode(
            @Valid @RequestBody EmailVerificationVerifyRequest emailVerificationVerifyRequest
    ){
        EmailVerificationVerifyResponse emailVerificationVerifyResponse = emailService.verifyVerificationCode(emailVerificationVerifyRequest);
        return ApiResponse.onSuccess(emailVerificationVerifyResponse);
    }
}