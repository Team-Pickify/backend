package com.pickyfy.pickyfy.web.dto;

import java.math.BigDecimal;
import java.util.List;

public record NearbyPlaceSearchCondition(
        BigDecimal latitude,
        BigDecimal longitude,
        Double distance,
        List<Long> categoryIds,
        List<Long> magazineIds
) {
}
