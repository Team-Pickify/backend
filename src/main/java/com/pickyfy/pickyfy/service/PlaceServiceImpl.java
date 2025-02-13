package com.pickyfy.pickyfy.service;

import com.pickyfy.pickyfy.exception.ExceptionHandler;
import com.pickyfy.pickyfy.web.apiResponse.error.ErrorStatus;
import com.pickyfy.pickyfy.domain.*;
import com.pickyfy.pickyfy.web.dto.NearbyPlaceSearchCondition;
import com.pickyfy.pickyfy.web.dto.request.NearbyPlaceSearchRequest;
import com.pickyfy.pickyfy.web.dto.request.PlaceCreateRequest;
import com.pickyfy.pickyfy.web.dto.response.PlaceSearchResponse;
import com.pickyfy.pickyfy.repository.*;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.util.Collections;
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
    private final PlaceCategoryRepository placeCategoryRepository;

    private final S3Service s3Service;

    @Override
    public List<PlaceSearchResponse> getUserSavePlace(String email) {
        User user = findUserByEmail(email);
        List<SavedPlace> savedPlaces = savedPlaceRepository.findAllByUserId(user.getId());
        if (savedPlaces.isEmpty()) {
            return Collections.emptyList();
        }

        return savedPlaces.stream()
                .map(savedPlace -> {
                    List<PlaceSavedPlace> placeSavedPlaces = placeSavedPlaceRepository.findAllBySavedPlaceId(savedPlace.getId());
                    if (placeSavedPlaces.isEmpty()){
                        return null;
                    }

                    PlaceSavedPlace mappingPlace = placeSavedPlaces.getFirst();
                    if (mappingPlace == null || mappingPlace.getPlace() == null) {
                        return null;
                    }

                    Place userSavedPlace = findPlaceById(mappingPlace.getPlace().getId());
                    Long userSavedPlaceId = userSavedPlace.getId();
                    Optional<Category> savedCategory = findCategoryByPlaceId(userSavedPlaceId);
                    Optional<Magazine> savedMagazine = findMagazineByPlaceId(userSavedPlaceId);

                    return PlaceSearchResponse.builder()
                            .placeId(userSavedPlace.getId())
                            .name(savedPlace.getName())
                            .shortDescription(savedPlace.getDescription())
                            .latitude(userSavedPlace.getLatitude())
                            .longitude(userSavedPlace.getLongitude())
                            .createdAt(savedPlace.getCreatedAt())
                            .updatedAt(savedPlace.getUpdatedAt())
                            .placeImageUrl(collectPlaceImages(placeSavedPlaces))
                            .categoryName(savedCategory.map(Category::getName).orElse(null))
                            .magazineTitle(savedMagazine.map(Magazine::getTitle).orElse(null))
                            .instagramLink(userSavedPlace.getInstagramLink())
                            .naverLink(userSavedPlace.getNaverplaceLink())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public PlaceSearchResponse getPlace(Long placeId) {
        Place searchPlace = findPlaceById(placeId);
        List<String> searchPlaceImageUrl = placeImageRepository.findAllByPlaceId(placeId);

        PlaceCategory searchPlaceCategory = placeCategoryRepository.findByPlaceId(searchPlace.getId());
        Category searchCategory = categoryRepository.findById(searchPlaceCategory.getCategory().getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorStatus.CATEGORY_NOT_FOUND.getMessage()));

        String categoryName = searchCategory.getName();

        PlaceMagazine searchPlaceMagazine = placeMagazineRepository.findByPlaceId(searchPlace.getId());
        Magazine searchMagazine = magazineRepository.findById(searchPlaceMagazine.getMagazine().getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorStatus.MAGAZINE_NOT_FOUND.getMessage()));
        String searchMagazineTitle = searchMagazine.getTitle();

        List<Long> placeImagesIdList = searchPlace.getPlaceImages().stream()
                .map(PlaceImage::getId)
                .toList();


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
                .placeImageId(placeImagesIdList)
                .build();
    }

    @Transactional
    public boolean togglePlaceUser(String email,Long placeId) {
        User user = findUserByEmail(email);
        Place place = findPlaceById(placeId);
        String placeName = place.getName();

        SavedPlace savedPlace = savedPlaceRepository.findByUserIdAndName(user.getId(), placeName)
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
        }

        PlaceSavedPlace placeSavedPlace = PlaceSavedPlace.builder()
                    .place(place)
                    .savedPlace(savedPlace)
                    .build();

        placeSavedPlaceRepository.save(placeSavedPlace);
        return true;
    }

    @Override
    public List<Place> searchNearbyPlaces(NearbyPlaceSearchRequest request) {
        NearbyPlaceSearchCondition condition = NearbyPlaceSearchCondition.from(request);
        return placeRepository.searchNearbyPlaces(condition);
    }

    @Transactional
    public Long createPlace(PlaceCreateRequest request, List<MultipartFile> imageList) {

        if (placeRepository.existsPlaceByName(request.name())) {
            throw new EntityExistsException(ErrorStatus.PLACE_NAME_DUPLICATED.getMessage());
        }

        Category category = categoryRepository.findById(request.categoryId()).orElseThrow(() ->
                new EntityNotFoundException(ErrorStatus.CATEGORY_NOT_FOUND.getMessage()));

        Magazine magazine = magazineRepository.findById(request.magazineId()).orElseThrow(() ->
                new EntityNotFoundException(ErrorStatus.MAGAZINE_NOT_FOUND.getMessage()));

        // 1. Place 먼저 저장
        Place newPlace = buildPlace(request);
        newPlace = placeRepository.save(newPlace);

        PlaceCategory newPlaceCategory = buildPlaceCategory(category, newPlace);
        placeCategoryRepository.save(newPlaceCategory);

        PlaceMagazine newPlaceMagazine = buildPlaceMagazine(magazine, newPlace);
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

    @Override
    @Transactional
    public Long updatePlace(Long placeId, PlaceCreateRequest request, List<MultipartFile> imageList) {
        Place place = findPlaceById(placeId);
        place.updatePlace(request);
        updatePlaceCategory(request.categoryId(), placeId, place);
        updatePlaceMagazine(request.magazineId(), placeId, place);
        updatePlaceImages(place, imageList);
        return place.getId();
    }

    @Override
    @Transactional
    public void deletePlace(Long placeId) {
        Place place = findPlaceById(placeId);
        for (PlaceImage image : place.getPlaceImages()) {
            s3Service.removeFile(image.getUrl());
        }
        placeRepository.delete(place);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlaceSearchResponse> getAllPlaces() {
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

                    List<Long> placeImagesIdList = place.getPlaceImages().stream()
                            .map(PlaceImage::getId)
                            .toList();

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
                            .placeImageId(placeImagesIdList)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private Optional<Category> findCategoryByPlaceId(Long userSavedPlaceId){
        PlaceCategory savedPlaceCategory = placeCategoryRepository.findByPlaceId(userSavedPlaceId);
        return Optional.ofNullable(savedPlaceCategory)
                .map(PlaceCategory::getCategory).flatMap(category -> categoryRepository.findById(category.getId()));
    }

    private Optional<Magazine> findMagazineByPlaceId(Long userSavedPlaceId){
        PlaceMagazine savedPlaceMagazine = placeMagazineRepository.findByPlaceId(userSavedPlaceId);
        return Optional.ofNullable(savedPlaceMagazine)
                .map(PlaceMagazine::getMagazine).flatMap(magazine -> magazineRepository.findById(magazine.getId()));
    }

    private List<String> collectPlaceImages(List<PlaceSavedPlace> placeSavedPlaces){
        return placeSavedPlaces.stream()
                .map(PlaceSavedPlace::getPlace)
                .flatMap(place -> place.getPlaceImages().stream())
                .map(PlaceImage::getUrl)
                .collect(Collectors.toList());
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));
    }

    private Place findPlaceById(Long placeId){
        return placeRepository.findById(placeId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorStatus.PLACE_NOT_FOUND.getMessage()));
    }

    private Place buildPlace(PlaceCreateRequest request){
        return Place.builder()
                .name(request.name())
                .longitude(request.longitude())
                .latitude(request.latitude())
                .address(request.address())
                .instagramLink(request.instagramLink())
                .naverplaceLink(request.naverPlaceLink())
                .shortDescription(request.shortDescription())
                .build();
    }

    private PlaceCategory buildPlaceCategory(Category category, Place newPlace){
        return PlaceCategory.builder()
                .category(category)
                .place(newPlace)
                .build();
    }

    private PlaceMagazine buildPlaceMagazine(Magazine magazine, Place newPlace){
        return PlaceMagazine.builder()
                .magazine(magazine)
                .place(newPlace)
                .build();
    }

    private void updatePlaceCategory(Long categoryId, Long placeId, Place place){
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorStatus.CATEGORY_NOT_FOUND.getMessage()));
        PlaceCategory placeCategory = placeCategoryRepository.findByPlaceId(placeId);
        placeCategory.updatePlaceCategory(place, category);
    }

    private void updatePlaceMagazine(Long magazineId, Long placeId, Place place){
        Magazine magazine = magazineRepository.findById(magazineId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorStatus.MAGAZINE_NOT_FOUND.getMessage()));
        PlaceMagazine placeMagazine = placeMagazineRepository.findByPlaceId(placeId);
        placeMagazine.updatePlaceMagazine(place, magazine);
    }

    private void updatePlaceImages(Place place, List<MultipartFile> imageList){
        List<PlaceImage> placeImages = placeImageRepository.findALlByPlace(place);
        placeImageRepository.deleteAll(placeImages);
        if(imageList != null && !imageList.isEmpty()) {
            place.updateImages(imageList, s3Service);
        }
    }
}
