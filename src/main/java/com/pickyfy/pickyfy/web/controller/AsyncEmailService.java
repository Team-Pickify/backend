package com.pickyfy.pickyfy.web.controller;

import com.pickyfy.pickyfy.common.Constant;
import com.pickyfy.pickyfy.common.util.RedisUtil;
import com.pickyfy.pickyfy.exception.ExceptionHandler;
import com.pickyfy.pickyfy.web.apiResponse.error.ErrorStatus;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class AsyncEmailService {

    private static final String ENCODING = "utf-8";
    private static final String EMAIL_TITLE = "[Pickyfy] 인증코드";
    private static final String VARIABLE_NAME = "code";
    private static final String TEMPLATE = "mail";

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final RedisUtil redisUtil;

    @Async
    public void sendVerificationEmail(String code, String email) {
        try {
            MimeMessage mimeMessage = createMimeMessage(code, email);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new ExceptionHandler(ErrorStatus._INTERNAL_SERVER_ERROR);
        }
        redisUtil.setData("email:" + email, code, Constant.EMAIL_TOKEN_EXPIRATION_TIME);
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
