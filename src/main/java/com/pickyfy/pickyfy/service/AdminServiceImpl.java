package com.pickyfy.pickyfy.service;

import com.pickyfy.pickyfy.apiPayload.code.status.ErrorStatus;
import com.pickyfy.pickyfy.common.Constant;
import com.pickyfy.pickyfy.common.util.JwtUtil;
import com.pickyfy.pickyfy.common.util.RedisUtil;
import com.pickyfy.pickyfy.domain.*;
import com.pickyfy.pickyfy.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import com.pickyfy.pickyfy.domain.Place;
import com.pickyfy.pickyfy.domain.PlaceImage;
import com.pickyfy.pickyfy.web.dto.request.PlaceCreateRequest;
import jakarta.persistence.EntityExistsException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService{



    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final AdminRepository adminRepository;
    private final PlaceRepository placeRepository;
    private final S3Service s3Service;
    private final PlaceImageRepository placeImageRepository;
    private final CategoryRepository categoryRepository;
    private final PlaceCategoryRepository placeCategoryRepository;
    private final MagazineRepository magazineRepository;
    private final PlaceMagazineRepository placeMagazineRepository;


    /**
     * 관리자 기능 (Place 생성)
     * @param request
     * @param imageList
     * @return
     */
    @Override
    @Transactional
    public Long createPlace(PlaceCreateRequest request, List<MultipartFile> imageList) {

        if (placeRepository.existsPlaceByName(request.name())) {
            throw new EntityExistsException("Place Already exists");
        }


        Place newPlace = Place.builder()
                .name(request.name())
                .longitude(request.longitude())
                .latitude(request.latitude())
                .address(request.address())
                .instagramLink(request.instagramLink())
                .naverplaceLink(request.naverPlaceLink())
                .shortDescription(request.shortDescription())
                .build();

        // Category, Magazine 매핑 저장
        Category createPlaceCategory = Category.builder()
                .type(request.categoryType())
                .build();
        categoryRepository.save(createPlaceCategory);

        PlaceCategory newPlaceCategory = PlaceCategory.builder()
                .category(createPlaceCategory)
                .place(newPlace)
                .build();
        placeCategoryRepository.save(newPlaceCategory);

        Magazine createPlaceMagazine = Magazine.builder()
                .title(request.magazineTitle())
                .build();
        magazineRepository.save(createPlaceMagazine);

        PlaceMagazine newPlaceMagazine = PlaceMagazine.builder()
                .magazine(createPlaceMagazine)
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

        PlaceCategory placeCategory = placeCategoryRepository.findByPlaceId(placeId);
        Optional<Category> category = categoryRepository.findById(placeCategory.getCategory().getId());

        PlaceMagazine placeMagazine = placeMagazineRepository.findByPlaceId(placeId);
        Optional<Magazine> magazine = magazineRepository.findById(placeMagazine.getMagazine().getId());

        category.get().update(request.categoryType());
        magazine.get().update(request.magazineTitle());


        place.updateImages(imageList, s3Service);
        return place.getId();
    }




    @Override
    @org.springframework.transaction.annotation.Transactional
    public void deletePlace(Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorStatus.PLACE_NOT_FOUND.getMessage()));

        PlaceCategory placeCategory = placeCategoryRepository.findByPlaceId(placeId);
        Optional<Category> category = categoryRepository.findById(placeCategory.getCategory().getId());

        PlaceMagazine placeMagazine = placeMagazineRepository.findByPlaceId(placeId);
        Optional<Magazine> magazine = magazineRepository.findById(placeMagazine.getMagazine().getId());

        for (PlaceImage image : place.getPlaceImages()) {
            s3Service.removeFile(image.getUrl());
        }
        categoryRepository.delete(category.get());
        placeCategoryRepository.delete(placeCategory);
        magazineRepository.delete(magazine.get());
        placeMagazineRepository.delete(placeMagazine);
        placeRepository.delete(place);
    }

    @Override
    @Transactional
    public void deletePlaceImages(Long placeImageId) {
        PlaceImage placeImage = placeImageRepository.findById(placeImageId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorStatus.IMAGE_INVALID.getMessage()));

        s3Service.removeFile(placeImage.getUrl());
        placeImageRepository.delete(placeImage);
    }

    @Override
    @Transactional
    public void logout(String accessToken){
        String adminName = jwtUtil.getPrincipal(accessToken);

        Long expiration = jwtUtil.getExpirationDate(accessToken);
        redisUtil.blacklistAccessToken(accessToken, expiration);

        String redisKey = Constant.REDIS_KEY_PREFIX + adminName;
        redisUtil.deleteRefreshToken(redisKey);
    }

}

