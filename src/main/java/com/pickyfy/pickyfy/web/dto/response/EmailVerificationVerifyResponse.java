package com.pickyfy.pickyfy.web.dto.response;

public record EmailVerificationVerifyResponse(
        String email,
        String emailToken
) {
}
