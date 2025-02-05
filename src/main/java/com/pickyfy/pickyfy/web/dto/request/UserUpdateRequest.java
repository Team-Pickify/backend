package com.pickyfy.pickyfy.web.dto.request;

import com.pickyfy.pickyfy.common.Constant;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.multipart.MultipartFile;

public record UserUpdateRequest (
    @Pattern(regexp = Constant.NICKNAME_REGX,
            message = "닉네임은 1~8자이어야 하며 한글, 영어, 숫자, 특수문자만 허용됩니다.")
    String nickname,

    MultipartFile profileImage
) {}
