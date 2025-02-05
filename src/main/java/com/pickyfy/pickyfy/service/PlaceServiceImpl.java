package com.pickyfy.pickyfy.service;

import com.pickyfy.pickyfy.web.apiResponse.error.ErrorStatus;
import com.pickyfy.pickyfy.domain.*;
import com.pickyfy.pickyfy.web.dto.NearbyPlaceSearchCondition;
import com.pickyfy.pickyfy.web.dto.request.PlaceCreateRequest;
import com.pickyfy.pickyfy.web.dto.response.PlaceSearchResponse;
import com.pickyfy.pickyfy.repository.*;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {

    private final SavedPlaceRepository savedPlaceRepository;
    private final PlaceRepository placeRepository;
    private final PlaceSavedPlaceRepository placeSavedPlaceRepository;
    private final PlaceImageRepository placeImageRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final MagazineRepository magazineRepository;
    private final PlaceMagazineRepository placeMagazineRepository;
    private final S3Service s3Service;
    private final PlaceCategoryRepository placeCategoryRepository;

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


                    Place userSavePlace = placeRepository.findById(mappingPlace.getPlace().getId()).orElseThrow(() -> new EntityNotFoundException("Place not found"));

                    // 유저가 저장한 Place 로 Category 조회
                    PlaceCategory savedPlaceCategory = placeCategoryRepository.findByPlaceId(userSavePlace.getId());
                    Optional<Category> savedCategory = categoryRepository.findById(savedPlaceCategory.getCategory().getId());

                    // 유저가 저장한 Place 로 Magazine 조회
                    PlaceMagazine savedPlaceMagazine = placeMagazineRepository.findByPlaceId(savedPlaceId);
                    Optional<Magazine> savedMagazine = magazineRepository.findById(savedPlaceMagazine.getMagazine().getId());


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
                            .categoryName(savedCategory.get().getName())
                            .magazineTitle(savedMagazine.get().getTitle())
                            .instagramLink(userSavePlace.getInstagramLink())
                            .naverLink(userSavePlace.getNaverplaceLink())
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

        //PlaceID 로 category 조회
        PlaceCategory searchPlaceCategory = placeCategoryRepository.findByPlaceId(searchPlace.getId());
        Optional<Category> searchCategory = categoryRepository.findById(searchPlaceCategory.getCategory().getId());
        String categoryName = searchCategory.get().getName();

        //PlaceID 로 magazine 조회
        PlaceMagazine searchPlaceMagazine = placeMagazineRepository.findByPlaceId(searchPlace.getId());
        Optional<Magazine> searchMagazine = magazineRepository.findById(searchPlaceMagazine.getMagazine().getId());
        String searchMagazineTitle = searchMagazine.get().getTitle();

        return PlaceSearchResponse.builder()
                .placeId(placeId)
                .placeImageUrl(searchPlaceImageUrl)
                .shortDescription(searchPlace.getShortDescription())
                .name(searchPlace.getName())
                .createdAt(searchPlace.getCreatedAt())
                .updatedAt(searchPlace.getUpdatedAt())
                .longitude(searchPlace.getLongitude())
                .latitude(searchPlace.getLatitude())
                .categoryName(categoryName)
                .magazineTitle(searchMagazineTitle)
                .instagramLink(searchPlace.getInstagramLink())
                .naverLink(searchPlace.getNaverplaceLink())
                .build();
    }


    /**
     * 유저 Place 저장 및 저장취소 (toggle)
     * @param userId
     * @param placeId
     * @return
     */
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

    @Override
    public List<Place> searchNearbyPlaces(BigDecimal lat, BigDecimal lon,
                                          Double distance,
                                          List<Long> categories,
                                          List<Long> magazines) {

        NearbyPlaceSearchCondition condition = new NearbyPlaceSearchCondition(lat, lon, distance, categories, magazines);

        return placeRepository.searchNearbyPlaces(condition);
    }

    /**
     * 관리자 기능 (Place 생성)
     * @param request
     * @param imageList
     * @return
     */
    @Transactional
    public Long createPlace(PlaceCreateRequest request, List<MultipartFile> imageList) {

        if (placeRepository.existsPlaceByName(request.name())) {
            throw new EntityExistsException("Place Already exists");
        }

        Category category = categoryRepository.findById(request.categoryId()).orElseThrow(() ->
                new EntityNotFoundException("Category not found"));

        Magazine magazine = magazineRepository.findById(request.magazineId()).orElseThrow(() ->
                new EntityNotFoundException("Magazine not found"));

        // 1. Place 먼저 저장
        Place newPlace = Place.builder()
                .name(request.name())
                .longitude(request.longitude())
                .latitude(request.latitude())
                .address(request.address())
                .instagramLink(request.instagramLink())
                .naverplaceLink(request.naverPlaceLink())
                .shortDescription(request.shortDescription())
                .build();

        newPlace = placeRepository.save(newPlace);

        PlaceCategory newPlaceCategory = PlaceCategory.builder()
                .category(category)
                .place(newPlace)
                .build();

        placeCategoryRepository.save(newPlaceCategory);

        PlaceMagazine newPlaceMagazine = PlaceMagazine.builder()
                .magazine(magazine)
                .place(newPlace)
                .build();

        placeMagazineRepository.save(newPlaceMagazine);

        List<PlaceImage> placeImages = new ArrayList<>();
        int maxImages = Math.min(imageList.size(), 5);

        for (int i = 0; i < maxImages; i++) {
            String imageUrl = s3Service.upload(imageList.get(i));
            PlaceImage newPlaceImage = PlaceImage.builder()
                    .place(newPlace)
                    .url(imageUrl)
                    .sequence(i)
                    .build();
            placeImages.add(newPlaceImage);
        }

        newPlace.getPlaceImages().addAll(placeImages);
        placeRepository.save(newPlace);

        return newPlace.getId();
    }

    /**
     * 관리자 기능(Place 수정)
     * @param placeId
     * @param request
     * @param imageList
     * @return
     */
    @Override
    @Transactional
    public Long updatePlace(Long placeId, PlaceCreateRequest request, List<MultipartFile> imageList) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorStatus.PLACE_NOT_FOUND.getMessage()));

        place.updatePlace(request.name(), request.address(), request.shortDescription(),
                request.instagramLink(), request.naverPlaceLink(), request.latitude(), request.longitude() );

        Category category = categoryRepository.findById(request.categoryId()).get();
        Magazine magazine = magazineRepository.findById(request.magazineId()).get();

        PlaceCategory placeCategory = placeCategoryRepository.findByPlaceId(placeId);

        PlaceMagazine placeMagazine = placeMagazineRepository.findByPlaceId(placeId);

        placeCategory.updatePlaceCategory(place, category);
        placeMagazine.updatePlaceMagazine(place, magazine);

        if(imageList != null && !imageList.isEmpty()) {
            place.updateImages(imageList, s3Service);
        }

        return place.getId();
    }


    /**
     * Place 삭제
     * @param placeId
     */
    @Override
    @Transactional
    public void deletePlace(Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorStatus.PLACE_NOT_FOUND.getMessage()));

        for (PlaceImage image : place.getPlaceImages()) {
            s3Service.removeFile(image.getUrl());
        }

        // 6. Place 삭제
        placeRepository.delete(place);
    }



    /**
     * PlaceImage 삭제
     * @param placeImageId
     */
    @Override
    @Transactional
    public void deletePlaceImages(Long placeImageId) {
        PlaceImage placeImage = placeImageRepository.findById(placeImageId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorStatus.IMAGE_INVALID.getMessage()));

        s3Service.removeFile(placeImage.getUrl());
        placeImageRepository.delete(placeImage);
    }


    @Override
    @Transactional(readOnly = true)
    public List<PlaceSearchResponse> getAdminAllPlace() {

        List<Place> allPlaceList = placeRepository.findAll();

        return allPlaceList.stream()
                .map(place -> {

                    PlaceCategory placeCategory = placeCategoryRepository.findByPlaceId(place.getId());
                    String categoryName = (placeCategory != null) ?
                            placeCategory.getCategory().getName() : "카테고리 없음";

                    PlaceMagazine placeMagazine = placeMagazineRepository.findByPlaceId(place.getId());
                    String magazineTitle = (placeMagazine != null) ?
                            placeMagazine.getMagazine().getTitle() : "매거진 없음";

                    List<String> placeImages = place.getPlaceImages().stream()
                            .map(PlaceImage::getUrl)
                            .collect(Collectors.toList());

                    return PlaceSearchResponse.builder()
                            .placeId(place.getId())
                            .name(place.getName())
                            .shortDescription(place.getShortDescription())
                            .latitude(place.getLatitude())
                            .longitude(place.getLongitude())
                            .createdAt(place.getCreatedAt())
                            .updatedAt(place.getUpdatedAt())
                            .placeImageUrl(placeImages)
                            .categoryName(categoryName)
                            .magazineTitle(magazineTitle)
                            .instagramLink(place.getInstagramLink())
                            .naverLink(place.getNaverplaceLink())
                            .build();
                })
                .collect(Collectors.toList());
    }

}
