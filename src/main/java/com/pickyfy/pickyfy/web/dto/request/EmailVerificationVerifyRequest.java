package com.pickyfy.pickyfy.web.dto.request;

import com.pickyfy.pickyfy.common.AllFieldsNotNull;

@AllFieldsNotNull
public record EmailVerificationVerifyRequest(
        String email,
        String code
) {
}
