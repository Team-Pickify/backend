package com.pickyfy.pickyfy.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EmailVerificationResponse(String email, String code){

    public static EmailVerificationResponse of(String email, String code) {
        return new EmailVerificationResponse(email, code);
    }
}
