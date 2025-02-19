package com.pickyfy.pickyfy.web.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record EmailVerificationSendRequest(
        @NotNull @Email String email
){}