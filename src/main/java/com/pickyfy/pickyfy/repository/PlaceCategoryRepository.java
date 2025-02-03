package com.pickyfy.pickyfy.repository;

import com.pickyfy.pickyfy.domain.PlaceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceCategoryRepository extends JpaRepository<PlaceCategory, Long> {
    PlaceCategory findByPlaceId(Long placeId);
}
