package com.pickyfy.pickyfy.web.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PlaceSearchResponse(
        Long placeId,
        String name,
        String shortDescription,
        String instagramLink,
        BigDecimal latitude,
        BigDecimal longitude,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<String> placeImageUrl,
        Long categoryId) {
}
