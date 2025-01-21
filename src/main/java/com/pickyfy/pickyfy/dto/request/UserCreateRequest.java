package com.pickyfy.pickyfy.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record UserCreateRequest(
        @Pattern(regexp = "^[a-zA-Z가-힣0-9!@#$%^&*()_+={}\\[\\]:;\"'<>,.?/-]{1,8}$", message = "닉네임은 1~8자, 한글, 영어, 숫자, 특수문자만 허용됩니다.")
        String nickname,
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+={}\\[\\]:;\"'<>,.?/-]).{8,}$", message = "비밀번호는 대소문자, 숫자, 특수문자를 포함하여 최소 8자 이상이어야 합니다.")
        String password,
        @Email
        String email,
        String emailToken
) {
}
