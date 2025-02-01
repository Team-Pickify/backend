package com.pickyfy.pickyfy.web.dto.request;

import jakarta.validation.constraints.Email;

public record EmailVerificationSendRequest(
        @Email String email
){}