package com.pickyfy.pickyfy.service;


import com.pickyfy.pickyfy.domain.Place;
import com.pickyfy.pickyfy.web.dto.request.NearbyPlaceSearchRequest;
import com.pickyfy.pickyfy.web.dto.request.PlaceCreateRequest;
import com.pickyfy.pickyfy.web.dto.response.PlaceSearchResponse;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface PlaceService {
    Long createPlace(PlaceCreateRequest request, List<MultipartFile> imageList);
    Long updatePlace(Long placeId, PlaceCreateRequest request, List<MultipartFile> imageList);
    void deletePlace(Long placeId);
    List<PlaceSearchResponse> getUserSavePlace(String userNickname);
    PlaceSearchResponse getPlace(Long placeId);
    boolean togglePlaceUser (String email, Long placeId);
    List<Place> searchNearbyPlaces(NearbyPlaceSearchRequest request);
    List<PlaceSearchResponse> getAllPlaces();
}
