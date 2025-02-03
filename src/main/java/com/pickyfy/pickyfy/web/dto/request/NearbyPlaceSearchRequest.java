package com.pickyfy.pickyfy.web.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

public record NearbyPlaceSearchRequest(
        @NotNull
        BigDecimal latitude,

        @NotNull
        BigDecimal longitude,

        @NotNull @Positive
        Double distance,

        List<Long> categoryIds,
        List<Long> magazineIds
) {
}
