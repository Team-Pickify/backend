package com.pickyfy.pickyfy.dto.response;

public record EmailVerificationVerifyResponse(
        String email,
        String emailToken
) {
}
