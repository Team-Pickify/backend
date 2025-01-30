package com.pickyfy.pickyfy.web.dto.request;

import lombok.Builder;

@Builder
public record PlaceSearchRequest(
        Long userId
){}
