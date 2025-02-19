package com.pickyfy.pickyfy.web.dto.request;

import com.pickyfy.pickyfy.domain.CategoryType;
import jakarta.validation.constraints.NotNull;

public record CategoryTypeRequest(
        @NotNull(message = "카테고리 타입은 필수입니다.")
        CategoryType categoryType
) {}
