package com.pickyfy.pickyfy.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EmailVerificationSendResponse(String email, String code){

    public static EmailVerificationSendResponse of(String email, String code) {
        return new EmailVerificationSendResponse(email, code);
    }
}
