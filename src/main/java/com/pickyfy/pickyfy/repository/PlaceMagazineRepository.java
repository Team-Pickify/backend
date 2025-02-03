package com.pickyfy.pickyfy.repository;

import com.pickyfy.pickyfy.domain.PlaceMagazine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceMagazineRepository extends JpaRepository<PlaceMagazine, Long> {
    PlaceMagazine findByPlaceId(Long placeId);
}
