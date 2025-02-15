package com.pickyfy.pickyfy.web.dto;

import com.pickyfy.pickyfy.domain.Place;

import java.util.List;

public record PlaceSearchResponseParams(
        Place place,
        List<String> placeImageUrls,
        String categoryName,
        String magazineTitle,
        String iconUrl
){}