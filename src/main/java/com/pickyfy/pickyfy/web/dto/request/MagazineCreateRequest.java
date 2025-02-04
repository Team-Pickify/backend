package com.pickyfy.pickyfy.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record MagazineCreateRequest(
        @NotBlank(message = "제목은 필수입니다")
        String title,

        String iconUrl
) {}
