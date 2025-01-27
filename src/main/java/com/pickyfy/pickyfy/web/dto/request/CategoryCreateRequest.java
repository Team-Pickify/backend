package com.pickyfy.pickyfy.web.dto.request;

import com.pickyfy.pickyfy.domain.CategoryType;
import jakarta.validation.constraints.NotBlank;

public record CategoryCreateRequest(
        @NotBlank(message = "Category type is required")
        CategoryType categoryType
) {}
