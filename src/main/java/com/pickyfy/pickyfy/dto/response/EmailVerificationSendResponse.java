package com.pickyfy.pickyfy.dto.response;

public record EmailVerificationSendResponse(
        String email,
        String code
){
}