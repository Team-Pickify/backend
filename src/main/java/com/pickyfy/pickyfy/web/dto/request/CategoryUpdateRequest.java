package com.pickyfy.pickyfy.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategoryUpdateRequest(
        @NotBlank(message = "Name is required")
        String name
) {}
