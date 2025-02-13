package com.pickyfy.pickyfy.web.controller;

import com.pickyfy.pickyfy.auth.details.CustomUserDetails;
import com.pickyfy.pickyfy.exception.ExceptionHandler;
import com.pickyfy.pickyfy.service.PlaceService;
import com.pickyfy.pickyfy.web.apiResponse.common.ApiResponse;
import com.pickyfy.pickyfy.web.apiResponse.error.ErrorStatus;
import com.pickyfy.pickyfy.web.apiResponse.success.SuccessStatus;
import com.pickyfy.pickyfy.web.dto.request.NearbyPlaceSearchRequest;
import com.pickyfy.pickyfy.web.dto.request.PlaceCreateRequest;
import com.pickyfy.pickyfy.web.dto.response.NearbyPlaceResponse;
import com.pickyfy.pickyfy.web.dto.response.PlaceSearchResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class PlaceController implements PlaceControllerApi {

    private final PlaceService placeService;

    @GetMapping("/places")
    public ApiResponse<List<PlaceSearchResponse>> getUserSavePlace() {
        String email = getUserEmail();
        List<PlaceSearchResponse> allPlace = placeService.getUserSavePlace(email);
        return ApiResponse.onSuccess(SuccessStatus.PLACES_RETRIEVED, allPlace);
    }

    @GetMapping("/places/{placeId}")
    public ApiResponse<PlaceSearchResponse> getPlace(@PathVariable Long placeId) {
        PlaceSearchResponse response = placeService.getPlace(placeId);
        return ApiResponse.onSuccess(SuccessStatus.PLACES_RETRIEVED, response);
    }

    @PatchMapping("/places/toggle")
    public ApiResponse<Void> togglePlaceUser(@RequestParam Long placeId) {
        String email = getUserEmail();
        boolean isSaved = placeService.togglePlaceUser(email,placeId);
        return ApiResponse.onSuccess(isSaved ? SuccessStatus.SAVE_PLACE_SUCCESS : SuccessStatus.DELETE_PLACE_SUCCESS, null);
    }

    @PostMapping("/places/nearby")
    public ApiResponse<List<NearbyPlaceResponse>> searchNearbyPlaces(@RequestBody NearbyPlaceSearchRequest request) {
        return ApiResponse.onSuccess(
                SuccessStatus.NEARBY_PLACES_RETRIEVED,
                placeService.searchNearbyPlaces(
                        request.latitude(),
                        request.longitude(),
                        request.distance(),
                        request.categoryIds(),
                        request.magazineIds()
                ).stream()
                .map(NearbyPlaceResponse::from)
                .toList()
        );
    }

    @PostMapping(value = "/admin/places", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Long> createPlace(
            @RequestPart(value = "image", required = false) List<MultipartFile> images,
            @RequestPart PlaceCreateRequest request) {
        Long id = placeService.createPlace(request, images);
        return ApiResponse.onSuccess(SuccessStatus.ADD_PLACE_SUCCESS, id);
    }

    @PatchMapping(value = "/admin/places/{placeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Long> updatePlace(
            @PathVariable Long placeId,
            @RequestPart("request") @Valid PlaceCreateRequest request,
            @RequestPart(value = "image", required = false) List<MultipartFile> images) {
        Long id = placeService.updatePlace(placeId, request, images);
        return ApiResponse.onSuccess(SuccessStatus.EDIT_PLACE_SUCCESS, id);
    }

    @DeleteMapping("/admin/places/{placeId}")
    public ApiResponse<Void> deletePlace(@PathVariable Long placeId) {
        placeService.deletePlace(placeId);
        return ApiResponse.onSuccess(SuccessStatus.DELETE_PLACE_SUCCESS, null);
    }

    @GetMapping(value = "/admin/places")
    public ApiResponse<List<PlaceSearchResponse>> getAllPlace() {
        List<PlaceSearchResponse> adminAllPlace = placeService.getAdminAllPlace();
        return ApiResponse.onSuccess(SuccessStatus.PLACES_RETRIEVED, adminAllPlace);
    }

    private String getUserEmail(){
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(CustomUserDetails.class::isInstance)
                .map(CustomUserDetails.class::cast)
                .map(CustomUserDetails::getUsername)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));
    }
}
