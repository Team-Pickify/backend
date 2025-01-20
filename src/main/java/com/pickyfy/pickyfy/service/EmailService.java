package com.pickyfy.pickyfy.service;

import com.pickyfy.pickyfy.dto.request.EmailVerificationSendRequest;
import com.pickyfy.pickyfy.dto.response.EmailVerificationSendResponse;

public interface EmailService {

    EmailVerificationSendResponse sendVerificationCode(EmailVerificationSendRequest request);
}