package com.pickyfy.pickyfy.service;

import com.pickyfy.pickyfy.web.apiResponse.error.ErrorStatus;
import com.pickyfy.pickyfy.common.util.JwtUtil;
import com.pickyfy.pickyfy.common.util.RedisUtil;
import com.pickyfy.pickyfy.web.controller.AsyncEmailService;
import com.pickyfy.pickyfy.web.dto.request.EmailVerificationSendRequest;
import com.pickyfy.pickyfy.web.dto.request.EmailVerificationVerifyRequest;
import com.pickyfy.pickyfy.web.dto.response.EmailVerificationSendResponse;
import com.pickyfy.pickyfy.web.dto.response.EmailVerificationVerifyResponse;
import com.pickyfy.pickyfy.exception.ExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final AsyncEmailService asyncEmailService;
    private final RedisUtil redisUtil;
    private final JwtUtil jwtUtil;

    @Override
    public EmailVerificationSendResponse sendAuthCode(EmailVerificationSendRequest request) {
        String email = request.email();
        String code = generateVerificationCode();
        asyncEmailService.sendVerificationEmail(code, email);
        return new EmailVerificationSendResponse(email, code);
    }

    @Override
    public EmailVerificationVerifyResponse verifyAuthCode(EmailVerificationVerifyRequest request) {
        String email = request.email();
        String inputCode = request.code();
        String savedCode = redisUtil.getData("email:" + email);
        redisUtil.deleteData("email:" + email);

        if(!savedCode.equals(inputCode)){
            throw new ExceptionHandler(ErrorStatus.AUTH_CODE_INVALID);
        }

        return new EmailVerificationVerifyResponse(email, jwtUtil.createEmailToken(email));
    }

    private String generateVerificationCode() {
        return Long.toString(ThreadLocalRandom.current().nextLong(100000L, 999999L));
    }
}