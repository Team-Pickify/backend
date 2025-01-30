package com.pickyfy.pickyfy.web.dto.request;

import com.pickyfy.pickyfy.common.Constant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record PasswordResetRequest(
        @Pattern(regexp = Constant.PASSWORD_REGX, message = "비밀번호는 대소문자, 숫자, 특수문자를 포함하여 최소 8자 이상이어야 합니다.")
        String password,

        @Email
        String email
) {
}
