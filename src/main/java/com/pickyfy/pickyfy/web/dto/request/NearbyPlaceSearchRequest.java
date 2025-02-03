package com.pickyfy.pickyfy.web.dto.request;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record NearbyPlaceSearchRequest(
        @NotNull
        BigDecimal latitude,

        @NotNull
        BigDecimal longitude,

        @NotNull
        Double distance, // TODO: 양수로만 검증

        List<Long> categoryIds,
        List<Long> magazineIds
) {
}
