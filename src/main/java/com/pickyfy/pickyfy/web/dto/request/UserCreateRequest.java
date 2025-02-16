package com.pickyfy.pickyfy.web.dto.request;

import com.pickyfy.pickyfy.common.AllFieldsNotNull;
import com.pickyfy.pickyfy.common.Constant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

@AllFieldsNotNull
public record UserCreateRequest(
        @Pattern(regexp = Constant.NICKNAME_REGX, message = "닉네임은 1~8자, 한글, 영어, 숫자, 특수문자만 허용됩니다.")
        String nickname,
        @Pattern(regexp = Constant.PASSWORD_REGX, message = "비밀번호는 대소문자, 숫자, 특수문자를 포함하여 최소 8자 이상이어야 합니다.")
        String password,
        @Email
        String email,
        String emailToken
) {
}
