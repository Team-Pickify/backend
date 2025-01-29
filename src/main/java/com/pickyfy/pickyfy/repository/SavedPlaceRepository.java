package com.pickyfy.pickyfy.repository;


import com.pickyfy.pickyfy.domain.SavedPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedPlaceRepository extends JpaRepository<SavedPlace, Long> {
    List<SavedPlace> findAllByUserId(Long userId);
    Optional<SavedPlace> findByUserIdAndName(Long userId, String placeName);

}
