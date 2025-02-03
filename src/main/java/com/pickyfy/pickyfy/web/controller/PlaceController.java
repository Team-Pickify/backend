package com.pickyfy.pickyfy.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pickyfy.pickyfy.apiPayload.ApiResponse;
//import com.pickyfy.pickyfy.auth.custom.CustomUserDetails;

import com.pickyfy.pickyfy.web.dto.request.PlaceCreateRequest;
import com.pickyfy.pickyfy.web.dto.response.PlaceSearchResponse;
import com.pickyfy.pickyfy.service.PlaceServiceImpl;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/places")
public class PlaceController implements PlaceControllerApi {
    private final PlaceServiceImpl placeService;


    /**
     * 특정 유저의 저장된 플레이스 조회
     */
    @GetMapping("/")
    public ApiResponse<List<PlaceSearchResponse>> getUserSavePlace(@RequestParam Long userId) {
        List<PlaceSearchResponse> allPlace = placeService.getUserSavePlace(userId);
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
    public ApiResponse<String> togglePlaceUser(@RequestParam Long userId, @RequestParam Long placeId) {
        boolean isSaved = placeService.togglePlaceUser(userId, placeId);
        return ApiResponse.onSuccess(isSaved ? "Place saved successfully." : "Place removed successfully.");
    }

    /**
     * 플레이스 생성
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Long> createPlace(
            @RequestPart(value = "image", required = false) List<MultipartFile> images,
            @RequestPart PlaceCreateRequest request) {
        Long id = placeService.createPlace(request, images);
        return ApiResponse.onSuccess(id);
    }

    /**
     * 플레이스 업데이트
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
     * 플레이스 삭제
     */
    @DeleteMapping("/{placeId}")
    public ApiResponse<Void> deletePlace(@PathVariable Long placeId) {
        placeService.deletePlace(placeId);
        return ApiResponse.onSuccess(null);
    }

    /**
     * 특정 플레이스의 이미지 삭제
     */
    @DeleteMapping("/images/{placeImageId}")
    public ApiResponse<Void> deletePlaceImages(@PathVariable Long placeId, @PathVariable Long placeImageId) {
        placeService.deletePlaceImages(placeImageId);
        return ApiResponse.onSuccess(null);
    }
}

