package com.pickyfy.pickyfy.web.dto.request;

import com.pickyfy.pickyfy.domain.CategoryType;
import jakarta.validation.constraints.NotNull;

public record CategoryUpdateRequest(
        @NotNull(message = "Category type is required")
        CategoryType categoryType
) {}
