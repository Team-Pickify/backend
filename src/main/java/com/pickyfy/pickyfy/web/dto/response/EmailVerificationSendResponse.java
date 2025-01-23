package com.pickyfy.pickyfy.web.dto.response;

public record EmailVerificationSendResponse(
        String email,
        String code
){
}