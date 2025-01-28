package com.pickyfy.pickyfy.repository;

import com.pickyfy.pickyfy.domain.Place;
import com.pickyfy.pickyfy.domain.PlaceSavedPlace;
import com.pickyfy.pickyfy.domain.SavedPlace;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;


public interface PlaceSavedPlaceRepository extends JpaRepository<PlaceSavedPlace, Long> {
    List<PlaceSavedPlace> findAllBySavedPlaceId(Long savedPlaceId);
    Optional<PlaceSavedPlace> findByPlaceAndSavedPlace(Place place, SavedPlace savedPlace);
    PlaceSavedPlace findBySavedPlaceId(Long savedPlaceId);
}
