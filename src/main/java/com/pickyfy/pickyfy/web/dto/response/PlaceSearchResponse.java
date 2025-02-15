package com.pickyfy.pickyfy.web.dto.response;

import com.pickyfy.pickyfy.domain.Category;
import com.pickyfy.pickyfy.domain.Magazine;
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
        List<Long> placeImageId,
        String categoryName,
        String magazineTitle,
        String naverLink,
        String iconUrl
) {
}
