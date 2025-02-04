package com.pickyfy.pickyfy.repository;

import com.pickyfy.pickyfy.domain.Place;
import com.pickyfy.pickyfy.web.dto.NearbyPlaceSearchCondition;
import java.util.List;

public interface PlaceRepositoryCustom {
    List<Place> searchNearbyPlaces(NearbyPlaceSearchCondition condition);
}
