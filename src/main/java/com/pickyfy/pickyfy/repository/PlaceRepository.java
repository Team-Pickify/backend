package com.pickyfy.pickyfy.repository;

import com.pickyfy.pickyfy.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface PlaceRepository extends JpaRepository<Place, Long> {
    boolean existsPlaceByName(String placeName);
}
