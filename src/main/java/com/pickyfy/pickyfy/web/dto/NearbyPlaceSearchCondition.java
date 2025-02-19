package com.pickyfy.pickyfy.web.dto;

import com.pickyfy.pickyfy.web.dto.request.NearbyPlaceSearchRequest;

import java.math.BigDecimal;
import java.util.List;

public record NearbyPlaceSearchCondition(
        BigDecimal latitude,
        BigDecimal longitude,
        Double distance,
        List<Long> categoryIds,
        List<Long> magazineIds
) {
    public static NearbyPlaceSearchCondition from(NearbyPlaceSearchRequest request){
        return new NearbyPlaceSearchCondition(
                request.latitude(),
                request.longitude(),
                request.distance(),
                request.categoryIds(),
                request.magazineIds()
        );
    }
}
