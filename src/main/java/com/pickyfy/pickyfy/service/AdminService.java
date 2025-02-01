package com.pickyfy.pickyfy.service;

import com.pickyfy.pickyfy.web.dto.request.PlaceCreateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdminService {
    Long createPlace(PlaceCreateRequest request, List<MultipartFile> imageList);
    Long updatePlace(Long placeId, PlaceCreateRequest request, List<MultipartFile> imageList);
    void deletePlace(Long placeId);
    void deletePlaceImages(Long placeImageId);
    void logout(String accessToken);
}
