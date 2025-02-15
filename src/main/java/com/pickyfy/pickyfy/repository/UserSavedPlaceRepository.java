package com.pickyfy.pickyfy.repository;

import com.pickyfy.pickyfy.domain.Place;
import com.pickyfy.pickyfy.domain.UserSavedPlace;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;


public interface UserSavedPlaceRepository extends JpaRepository<UserSavedPlace, Long> {
//    List<UserSavedPlace> findAllBySavedPlaceId(Long savedPlaceId);
//    Optional<UserSavedPlace> findByPlaceAndSavedPlace(Place place, UserSavedPlace userSavedPlace);
//    UserSavedPlace findBySavedPlaceId(Long savedPlaceId);
//    UserSavedPlace findByPlaceId(Long placeId);
    List<UserSavedPlace> findAllByUserId(Long id);
    UserSavedPlace findByUserId(Long userId);

    Optional<UserSavedPlace> findByUserIdAndPlaceId(Long userId, Long placeId);
}
