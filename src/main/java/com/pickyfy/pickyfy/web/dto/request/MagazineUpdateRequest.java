package com.pickyfy.pickyfy.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public record MagazineUpdateRequest(
        @NotBlank(message = "제목은 필수입니다")
        String title,

        MultipartFile iconFile
) {}
