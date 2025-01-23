package com.pickyfy.pickyfy.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategoryCreateRequest(
        @NotBlank(message = "Name is required")
        String name
) {}
