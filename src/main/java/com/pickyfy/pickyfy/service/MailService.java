package com.pickyfy.pickyfy.service;

import com.pickyfy.pickyfy.dto.request.EmailVerificationRequest;
import com.pickyfy.pickyfy.dto.response.EmailVerificationResponse;

public interface MailService {

    EmailVerificationResponse sendVerificationCode(EmailVerificationRequest request);
}