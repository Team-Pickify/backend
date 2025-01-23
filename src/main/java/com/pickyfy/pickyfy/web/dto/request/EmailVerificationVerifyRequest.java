package com.pickyfy.pickyfy.web.dto.request;

public record EmailVerificationVerifyRequest(
        String email,
        String code
) {
}
