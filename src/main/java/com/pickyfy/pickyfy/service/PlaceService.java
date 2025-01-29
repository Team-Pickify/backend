package com.pickyfy.pickyfy.service;

import com.pickyfy.pickyfy.web.dto.response.PlaceSearchResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PlaceService {

    List<PlaceSearchResponse> getUserSavePlace(Long userId);
    PlaceSearchResponse getPlace(Long placeId);
    boolean togglePlaceUser (Long userId, Long placeId);
}
