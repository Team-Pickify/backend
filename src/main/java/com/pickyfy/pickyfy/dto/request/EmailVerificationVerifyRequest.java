package com.pickyfy.pickyfy.dto.request;

public record EmailVerificationVerifyRequest(
        String email,
        String code
) {
}
