package com.pickyfy.pickyfy.service;

import com.pickyfy.pickyfy.web.dto.request.EmailVerificationSendRequest;
import com.pickyfy.pickyfy.web.dto.request.EmailVerificationVerifyRequest;
import com.pickyfy.pickyfy.web.dto.response.EmailVerificationSendResponse;
import com.pickyfy.pickyfy.web.dto.response.EmailVerificationVerifyResponse;

public interface EmailService {
    EmailVerificationSendResponse sendAuthCode(EmailVerificationSendRequest request);
    EmailVerificationVerifyResponse verifyAuthCode(EmailVerificationVerifyRequest emailVerificationVerifyRequest);
}