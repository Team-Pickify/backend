package com.pickyfy.pickyfy.web.dto.response;

import com.pickyfy.pickyfy.domain.Magazine;
import java.time.LocalDateTime;

public record MagazineResponse(
        Long id,
        String title,
        String iconUrl,
        LocalDateTime createdAt
) {
    public static MagazineResponse from(Magazine magazine) {
        return new MagazineResponse(
                magazine.getId(),
                magazine.getTitle(),
                magazine.getIconUrl(),
                magazine.getCreatedAt()
        );
    }
}
