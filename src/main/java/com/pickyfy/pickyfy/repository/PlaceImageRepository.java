package com.pickyfy.pickyfy.repository;

import com.pickyfy.pickyfy.domain.Place;
import com.pickyfy.pickyfy.domain.PlaceImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlaceImageRepository extends JpaRepository<PlaceImage, Long> {
    @Query("SELECT pi.url FROM PlaceImage pi WHERE pi.place.id = :placeId")
    List<String> findAllByPlaceId(@Param("placeId") Long placeId);

    List<PlaceImage> findALlByPlace(Place place);
}
