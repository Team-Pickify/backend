package com.pickyfy.pickyfy.web.dto.request;

import jakarta.validation.constraints.Pattern;

public record UserUpdateRequest (
    @Pattern(regexp = "^[a-zA-Z가-힣0-9!@#$%^&*()_+={}\\[\\]:;\"'<>,.?/-]{1,8}$",
            message = "닉네임은 1~8자, 한글, 영어, 숫자, 특수문자만 허용됩니다.")
    String nickname,

    //TODO: 이미지 URL 정규식 설정
    String profileImage
) {}
