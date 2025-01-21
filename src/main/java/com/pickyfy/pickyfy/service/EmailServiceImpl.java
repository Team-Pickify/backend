package com.pickyfy.pickyfy.service;

import com.pickyfy.pickyfy.apiPayload.code.status.ErrorStatus;
import com.pickyfy.pickyfy.common.util.JwtUtil;
import com.pickyfy.pickyfy.common.util.RedisUtil;
import com.pickyfy.pickyfy.dto.request.EmailVerificationSendRequest;
import com.pickyfy.pickyfy.dto.request.EmailVerificationVerifyRequest;
import com.pickyfy.pickyfy.dto.response.EmailVerificationSendResponse;
import com.pickyfy.pickyfy.dto.response.EmailVerificationVerifyResponse;
import com.pickyfy.pickyfy.exception.handler.ExceptionHandler;
import com.pickyfy.pickyfy.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private static final String ENCODING = "utf-8";
    private static final String EMAIL_TITLE = "[Pickyfy] 인증코드";
    private static final String VARIABLE_NAME = "code";
    private static final String TEMPLATE = "mail";
    private static final Long EXPIRATION_TIME = 180L;

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final UserRepository userRepository;
    private final RedisUtil redisUtil;
    private final JwtUtil jwtUtil;

    @Transactional
    @Override
    public EmailVerificationSendResponse sendVerificationCode(EmailVerificationSendRequest request) {
        String email = request.email();
        validateEmailDuplicated(email);

        String code = generateVerificationCode();
        sendVerificationEmail(code, email);

        return new EmailVerificationSendResponse(email, code);
    }

    @Transactional
    @Override
    public EmailVerificationVerifyResponse verifyVerificationCode(EmailVerificationVerifyRequest request) {
        String email = request.email();
        String inputCode = request.code();
        String savedCode = redisUtil.getData("email:" + email);

        if(!savedCode.equals(inputCode)){
            throw new ExceptionHandler(ErrorStatus.AUTH_CODE_INVALID);
        }
        return new EmailVerificationVerifyResponse(email, jwtUtil.createEmailToken(email));
    }

    private void validateEmailDuplicated(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ExceptionHandler(ErrorStatus.EMAIL_DUPLICATED);
        }
    }

    private String generateVerificationCode() {
        return Long.toString(ThreadLocalRandom.current().nextLong(100000L, 999999L));
    }

    private void sendVerificationEmail(String code, String email) {
        try {
            MimeMessage mimeMessage = createMimeMessage(code, email);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new ExceptionHandler(ErrorStatus._INTERNAL_SERVER_ERROR);
        }

        redisUtil.setDataExpire("email:" + email, code, EXPIRATION_TIME);
    }

    private MimeMessage createMimeMessage(String code, String email) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, ENCODING);
        helper.setSubject(EMAIL_TITLE);
        helper.setTo(email);

        Context context = new Context();
        context.setVariable(VARIABLE_NAME, code);
        String htmlContent = templateEngine.process(TEMPLATE, context);
        helper.setText(htmlContent, true);

        return mimeMessage;
    }
}