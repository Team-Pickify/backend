package com.pickyfy.pickyfy.service;

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
    void deletePlaceImages(Long placeImageId);
    List<PlaceSearchResponse> getUserSavePlace(Long userId);
    PlaceSearchResponse getPlace(Long placeId);
    boolean togglePlaceUser (Long userId, Long placeId);
}
