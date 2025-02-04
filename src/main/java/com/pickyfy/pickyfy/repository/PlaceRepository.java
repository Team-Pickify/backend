package com.pickyfy.pickyfy.repository;

import com.pickyfy.pickyfy.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PlaceRepository extends JpaRepository<Place, Long>, PlaceRepositoryCustom {
    boolean existsPlaceByName(String placeName);
}
