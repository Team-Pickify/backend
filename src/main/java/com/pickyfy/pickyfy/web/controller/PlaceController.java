package com.pickyfy.pickyfy.web.controller;

import com.pickyfy.pickyfy.auth.details.CustomUserDetails;
import com.pickyfy.pickyfy.exception.handler.ExceptionHandler;
import com.pickyfy.pickyfy.service.PlaceService;
import com.pickyfy.pickyfy.web.apiResponse.common.ApiResponse;
import com.pickyfy.pickyfy.web.apiResponse.error.ErrorStatus;
import com.pickyfy.pickyfy.web.dto.request.NearbyPlaceSearchRequest;
import com.pickyfy.pickyfy.web.dto.request.PlaceCreateRequest;
import com.pickyfy.pickyfy.web.dto.response.NearbyPlaceResponse;
import com.pickyfy.pickyfy.web.dto.response.PlaceSearchResponse;
import com.pickyfy.pickyfy.service.PlaceServiceImpl;
import com.pickyfy.pickyfy.web.dto.response.UserInfoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/places")
public class PlaceController implements PlaceControllerApi {
    private final PlaceService placeService;


    /**
     * 특정 유저의 저장된 플레이스 조회
     */
    @GetMapping
    public ApiResponse<List<PlaceSearchResponse>> getUserSavePlace() {
        String email = getUserEmail();
        List<PlaceSearchResponse> allPlace = placeService.getUserSavePlace(email);

        return ApiResponse.onSuccess(allPlace);
    }

    /**
     * 특정 place 조회
     */
    @GetMapping("/{placeId}")
    public ApiResponse<PlaceSearchResponse> getPlace(@PathVariable Long placeId) {
        PlaceSearchResponse response = placeService.getPlace(placeId);
        return ApiResponse.onSuccess(response);
    }

    /**
     * 플레이스 저장/삭제 토글
     */
    @PatchMapping("/toggle")
    public ApiResponse<String> togglePlaceUser(@RequestParam Long placeId) {
        String email = getUserEmail();
        boolean isSaved = placeService.togglePlaceUser(email,placeId);
        return ApiResponse.onSuccess(isSaved ? "Place saved successfully." : "Place removed successfully.");
    }

    @PostMapping("/nearby")
    public ApiResponse<List<NearbyPlaceResponse>> searchNearbyPlaces(@RequestBody NearbyPlaceSearchRequest request) {
        return ApiResponse.onSuccess(
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

    /**
     * 관리자 페이지에서 모든 Place 조회
     * @param
     * @return
     */

    private String getUserEmail(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null){
            throw new ExceptionHandler(ErrorStatus.USER_NOT_FOUND);
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/places")
class AdminPlaceController implements AdminControllerAPi{
    private final PlaceServiceImpl placeService;

    /**
     * 플레이스 생성 (관리자)
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Long> createPlace(
            @RequestPart(value = "image", required = false) List<MultipartFile> images,
            @RequestPart PlaceCreateRequest request) {
        Long id = placeService.createPlace(request, images);
        return ApiResponse.onSuccess(id);
    }

    /**
     * 플레이스 업데이트 (관리자)
     */
    @PatchMapping(value = "/{placeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Long> updatePlace(
            @PathVariable Long placeId,
            @RequestPart("request") @Valid PlaceCreateRequest request,
            @RequestPart(value = "image", required = false) List<MultipartFile> images) {
        Long id = placeService.updatePlace(placeId, request, images);
        return ApiResponse.onSuccess(id);
    }

    /**
     * 플레이스 삭제 (관리자)
     */
    @DeleteMapping("/{placeId}")
    public ApiResponse<Void> deletePlace(@PathVariable Long placeId) {
        placeService.deletePlace(placeId);
        return ApiResponse.onSuccess(null);
    }

    /**
     * 특정 플레이스의 이미지 삭제 (관리자)
     */
    @DeleteMapping("/images/{placeImageId}")
    public ApiResponse<Void> deletePlaceImages(@PathVariable Long placeId, @PathVariable Long placeImageId) {
        placeService.deletePlaceImages(placeImageId);
        return ApiResponse.onSuccess(null);
    }

    /**
     * 관리자 페이지에서 모든 Place 조회
     */
    @GetMapping("/")
    public ApiResponse<List<PlaceSearchResponse>> getAllPlace() {
        List<PlaceSearchResponse> adminAllPlace = placeService.getAdminAllPlace();
        return ApiResponse.onSuccess(adminAllPlace);
    }
}

