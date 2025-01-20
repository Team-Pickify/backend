package com.pickyfy.pickyfy.controller;

import com.pickyfy.pickyfy.apiPayload.ApiResponse;
import com.pickyfy.pickyfy.dto.request.EmailVerificationRequest;
import com.pickyfy.pickyfy.dto.response.EmailVerificationResponse;
import com.pickyfy.pickyfy.service.MailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/emails/verification")
public class EmailVerificationController implements EmailVerificationControllerApi {

    private final MailService mailService;

    @PostMapping("/send")
    public ApiResponse<EmailVerificationResponse> sendVerificationCode(
            @Valid @RequestBody EmailVerificationRequest emailVerificationRequest
    ){
        EmailVerificationResponse emailVerificationResponse = mailService.sendVerificationCode(emailVerificationRequest);
        return ApiResponse.onSuccess(emailVerificationResponse);
    }
}