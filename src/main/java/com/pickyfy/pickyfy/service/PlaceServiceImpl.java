package com.pickyfy.pickyfy.service;

import com.pickyfy.pickyfy.apiPayload.code.status.ErrorStatus;
import com.pickyfy.pickyfy.domain.*;
import com.pickyfy.pickyfy.web.dto.response.PlaceSearchResponse;
import com.pickyfy.pickyfy.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {

    final private SavedPlaceRepository savedPlaceRepository;
    final private PlaceRepository placeRepository;
    final private PlaceSavedPlaceRepository placeSavedPlaceRepository;
    final private PlaceImageRepository placeImageRepository;
    final private UserRepository userRepository;


    /**
     * 특정 유저가 저장한 Place 전체 조회
     * @param userId
     * @return
     */
    @Override
    public List<PlaceSearchResponse> getUserSavePlace(Long userId) {

        List<SavedPlace> allPlaceList = savedPlaceRepository.findAllByUserId(userId);

        return allPlaceList.stream()
                .map(savedPlace -> {

                    List<PlaceSavedPlace> placeSavedPlaces = placeSavedPlaceRepository.findAllBySavedPlaceId(savedPlace.getId());
                    Long savedPlaceId = savedPlace.getId();
                    PlaceSavedPlace mappingPlace = placeSavedPlaceRepository.findBySavedPlaceId(savedPlaceId);

                    Place userSavePlace = placeRepository.findById(mappingPlace.getPlace().getId()).orElseThrow(() -> new EntityNotFoundException("Place not found"));;

                    List<Place> places = placeSavedPlaces.stream()
                            .map(PlaceSavedPlace::getPlace)
                            .toList();


                    List<String> placeImages = places.stream()
                            .flatMap(place -> place.getPlaceImages().stream())
                            .map(PlaceImage::getUrl)
                            .collect(Collectors.toList());


                    return PlaceSearchResponse.builder()
                            .placeId(userSavePlace.getId())
                            .name(savedPlace.getName())
                            .shortDescription(savedPlace.getDescription())
                            .latitude(userSavePlace.getLatitude())
                            .longitude(userSavePlace.getLongitude())
                            .createdAt(savedPlace.getCreatedAt())
                            .updatedAt(savedPlace.getUpdatedAt())
                            .placeImageUrl(placeImages)
                            .build();
                })
                .collect(Collectors.toList());
    }


    /**
     * 특정 플레이스 조회
     * @param placeId
     * @return
     */
    @Override
    public PlaceSearchResponse getPlace(Long placeId) {
        Place searchPlace = placeRepository.findById(placeId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorStatus.PLACE_NOT_FOUND.getMessage()));
        List<String> searchPlaceImageUrl = placeImageRepository.findAllByPlaceId(placeId);

        return PlaceSearchResponse.builder()
                .placeId(placeId)
                .placeImageUrl(searchPlaceImageUrl)
                .shortDescription(searchPlace.getShortDescription())
                .name(searchPlace.getName())
                .createdAt(searchPlace.getCreatedAt())
                .updatedAt(searchPlace.getUpdatedAt())
                .longitude(searchPlace.getLongitude())
                .latitude(searchPlace.getLatitude())
                .instagramLink(searchPlace.getInstagramLink())
                .build();
    }


    @Transactional
    public boolean togglePlaceUser(Long userId, Long placeId) {

        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorStatus.PLACE_NOT_FOUND.getMessage()));

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(ErrorStatus.USER_NOT_FOUND.getMessage()));

        String placeName = place.getName();

        SavedPlace savedPlace = savedPlaceRepository.findByUserIdAndName(userId, placeName)
                .orElseGet(() -> {
                    SavedPlace newSavedPlace = SavedPlace.builder()
                            .name(place.getName())
                            .description(place.getShortDescription())
                            .isPublic(true)
                            .user(user)
                            .build();
                    return savedPlaceRepository.save(newSavedPlace);
                });

        Optional<PlaceSavedPlace> existingRelation = placeSavedPlaceRepository.findByPlaceAndSavedPlace(place, savedPlace);

        if (existingRelation.isPresent()) {

            placeSavedPlaceRepository.delete(existingRelation.get());
            savedPlaceRepository.delete(savedPlace);
            return false;
        } else {
            PlaceSavedPlace placeSavedPlace = PlaceSavedPlace.builder()
                    .place(place)
                    .savedPlace(savedPlace)
                    .build();
            placeSavedPlaceRepository.save(placeSavedPlace);
            return true;
        }
    }
}
