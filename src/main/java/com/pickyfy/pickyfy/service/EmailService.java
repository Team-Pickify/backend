package com.pickyfy.pickyfy.service;

import com.pickyfy.pickyfy.dto.request.EmailVerificationSendRequest;
import com.pickyfy.pickyfy.dto.request.EmailVerificationVerifyRequest;
import com.pickyfy.pickyfy.dto.response.EmailVerificationSendResponse;
import com.pickyfy.pickyfy.dto.response.EmailVerificationVerifyResponse;

public interface EmailService {

    EmailVerificationSendResponse sendAuthCode(EmailVerificationSendRequest request);

    EmailVerificationVerifyResponse verifyAuthCode(EmailVerificationVerifyRequest emailVerificationVerifyRequest);
}