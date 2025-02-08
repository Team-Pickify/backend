package com.pickyfy.pickyfy.service;


import com.pickyfy.pickyfy.domain.Place;
import com.pickyfy.pickyfy.web.dto.request.PlaceCreateRequest;
import com.pickyfy.pickyfy.web.dto.response.PlaceSearchResponse;
import java.math.BigDecimal;

import com.pickyfy.pickyfy.web.dto.response.UserInfoResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

    @Service
    public interface PlaceService {
        Long createPlace(PlaceCreateRequest request, List<MultipartFile> imageList);
        Long updatePlace(Long placeId, PlaceCreateRequest request, List<MultipartFile> imageList);
        void deletePlace(Long placeId);
        void deletePlaceImages(Long placeImageId);
        List<PlaceSearchResponse> getUserSavePlace(String userNickname);
        PlaceSearchResponse getPlace(Long placeId);
        boolean togglePlaceUser (String userNickname, Long placeId);
        List<Place> searchNearbyPlaces(BigDecimal lat, BigDecimal lon, Double distance, List<Long> categories, List<Long> magazines);
        List<PlaceSearchResponse> getAdminAllPlace();
        UserInfoResponse getUser(String accessToken);
    }
