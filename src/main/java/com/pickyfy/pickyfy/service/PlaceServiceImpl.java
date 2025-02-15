package com.pickyfy.pickyfy.service;

import com.pickyfy.pickyfy.exception.ExceptionHandler;
import com.pickyfy.pickyfy.web.apiResponse.error.ErrorStatus;
import com.pickyfy.pickyfy.domain.*;
import com.pickyfy.pickyfy.web.dto.NearbyPlaceSearchCondition;
import com.pickyfy.pickyfy.web.dto.PlaceSearchResponseParams;
import com.pickyfy.pickyfy.web.dto.request.NearbyPlaceSearchRequest;
import com.pickyfy.pickyfy.web.dto.request.PlaceCreateRequest;
import com.pickyfy.pickyfy.web.dto.response.PlaceSearchResponse;
import com.pickyfy.pickyfy.repository.*;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

import java.util.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {

    private final PlaceRepository placeRepository;
    private final UserSavedPlaceRepository userSavedPlaceRepository;
    private final PlaceImageRepository placeImageRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final MagazineRepository magazineRepository;
    private final PlaceMagazineRepository placeMagazineRepository;
    private final PlaceCategoryRepository placeCategoryRepository;

    private final S3Service s3Service;

    /**
     * 특정 유저가 저장한 Place 전체 조회
     */
    @Override
    public List<PlaceSearchResponse> getUserSavePlace(String email) {
        // 유저 조회
        User user = findUserByEmail(email);
        List<UserSavedPlace> allUserSavedPlaceList = userSavedPlaceRepository.findAllByUserId(user.getId());

        if (allUserSavedPlaceList.isEmpty()) {
            return Collections.emptyList();
        }

        List<Place> allPlaceList = allUserSavedPlaceList.stream()
                .map(UserSavedPlace::getPlace)
                .toList();

        return allPlaceList.stream()
                .map(place -> {
                    // 유저가 저장한 Place의 카테고리 조회
                    Optional<PlaceCategory> savedPlaceCategory = Optional.ofNullable(placeCategoryRepository.findByPlaceId(place.getId()));
                    Optional<Category> savedCategory = savedPlaceCategory.flatMap(pc -> categoryRepository.findById(pc.getCategory().getId()));

                    // 유저가 저장한 Place의 매거진 조회
                    Optional<PlaceMagazine> savedPlaceMagazine = Optional.ofNullable(placeMagazineRepository.findByPlaceId(place.getId()));
                    Optional<Magazine> savedMagazine = savedPlaceMagazine.flatMap(pm -> magazineRepository.findById(pm.getMagazine().getId()));

                    // 유저가 저장한 Place의 이미지 조회
                    List<String> placeImagesUrl = placeImageRepository.findAllByPlaceId(place.getId());

                    return PlaceSearchResponse.from(new PlaceSearchResponseParams(
                                    place,
                                    placeImagesUrl,
                                    savedCategory.map(Category::getName).orElse(null),
                                    savedMagazine.map(Magazine::getTitle).orElse(null),
                                    savedMagazine.map(Magazine::getIconUrl).orElse(null))
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * 특정 플레이스 조회
     */
    @Override
    public PlaceSearchResponse getPlace(Long placeId) {
        Place place = findPlaceById(placeId);
        List<String> searchPlaceImageUrl = placeImageRepository.findAllByPlaceId(placeId);

        Category category = findCategoryByPlaceId(placeId);
        Magazine magazine = findMagazineByPlaceId(placeId);

        return PlaceSearchResponse.from( new PlaceSearchResponseParams(
                place,
                searchPlaceImageUrl,
                category.getName(),
                magazine.getTitle(),
                magazine.getIconUrl())
        );
    }

    /**
     * 유저 Place 저장 및 저장취소 (toggle)
     */
    @Transactional
    public boolean togglePlaceUser(String email, Long placeId) {
        Place place = findPlaceById(placeId);
        User user = findUserByEmail(email);

        Optional<UserSavedPlace> userSavedPlace = userSavedPlaceRepository.findByUserIdAndPlaceId(user.getId(), place.getId());

        return userSavedPlace.map(savedPlace -> {
            userSavedPlaceRepository.delete(savedPlace);
            return false;
        }).orElseGet(() -> {
            userSavedPlaceRepository.save(new UserSavedPlace(user, place));
            return true;
        });
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
        placeRepository.save(newPlace);

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
                    String categoryName = (placeCategory != null) ? placeCategory.getCategory().getName() : "카테고리 없음";

                    PlaceMagazine placeMagazine = placeMagazineRepository.findByPlaceId(place.getId());
                    String magazineTitle = (placeMagazine != null) ? placeMagazine.getMagazine().getTitle() : "매거진 없음";

                    List<String> placeImages = place.getPlaceImages().stream()
                            .map(PlaceImage::getUrl)
                            .collect(Collectors.toList());

                    Objects.requireNonNull(placeMagazine, "Place Magazine is Null");
                    return PlaceSearchResponse.from(new PlaceSearchResponseParams(
                            place,
                            placeImages,
                            categoryName,
                            magazineTitle,
                            placeMagazine.getMagazine().getIconUrl())
                    );
                })
                .collect(Collectors.toList());
    }

    private Category findCategoryByPlaceId(Long placeId){
        PlaceCategory savedPlaceCategory = placeCategoryRepository.findByPlaceId(placeId);
        if (savedPlaceCategory == null || savedPlaceCategory.getCategory() == null) {
            throw new EntityNotFoundException(ErrorStatus.CATEGORY_NOT_FOUND.getMessage());
        }
        Category category = savedPlaceCategory.getCategory();
        return categoryRepository.findById(category.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorStatus.CATEGORY_NOT_FOUND.getMessage()));
    }

    private Magazine findMagazineByPlaceId(Long placeId){
        PlaceMagazine searchPlaceMagazine = placeMagazineRepository.findByPlaceId(placeId);
        if (searchPlaceMagazine == null || searchPlaceMagazine.getMagazine() == null) {
            throw new EntityNotFoundException(ErrorStatus.MAGAZINE_NOT_FOUND.getMessage());
        }
        return magazineRepository.findById(searchPlaceMagazine.getMagazine().getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorStatus.MAGAZINE_NOT_FOUND.getMessage()));
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
