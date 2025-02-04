package com.pickyfy.pickyfy.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pickyfy.pickyfy.apiPayload.ApiResponse;
import com.pickyfy.pickyfy.web.dto.request.PlaceCreateRequest;
import com.pickyfy.pickyfy.web.dto.response.PlaceSearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Tag(name = "장소")
public interface PlaceControllerApi {

    @Operation(summary = "유저가 저장한 Place 전체 조회 API", description = "유저가 저장한 Place 들을 리스트로 전체 조회하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @GetMapping("/")
    ApiResponse<List<PlaceSearchResponse>> getUserSavePlace(@RequestParam Long userId);

    @Operation(summary = "특정 Place 를 조회하는 API", description = "특정 Place 정보를 조회하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @GetMapping("/{placeId}")
    ApiResponse<PlaceSearchResponse> getPlace(@PathVariable("placeId") Long placeId);

    @Operation(summary = "유저 Place 저장 API", description = "유저가 Place 를 저장하거나 삭제하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @PatchMapping("/toggle")
    ApiResponse<String> togglePlaceUser(@RequestParam Long userId, @RequestParam Long placeId);

    @Operation(summary = "Place 생성", description = "Place 를 생성하고 최대 5장의 PlaceImage 를 업로드합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<Long> createPlace(
            @RequestPart(value = "image", required = false) List<MultipartFile> images,
            @RequestPart PlaceCreateRequest request);

    @Operation(summary = "Place 수정", description = "Place 를 수정하고 5장의 PlaceImage 를 수정하여 업로드합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @PatchMapping(value = "/{placeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<Long> updatePlace(
            @PathVariable Long placeId,
            @RequestPart("request") @Valid PlaceCreateRequest request,
            @RequestPart(value = "image", required = false) List<MultipartFile> images);

    @Operation(summary = "Place 삭제", description = "Place 와 PlaceImage 모두 삭제 합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @DeleteMapping("/{placeId}")
    ApiResponse<Void> deletePlace(@PathVariable Long placeId);

    @Operation(summary = "PlaceImage 삭제", description = "PlaceImage 선택 삭제 합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @DeleteMapping("/images/{placeImageId}")
    ApiResponse<Void> deletePlaceImages(@PathVariable Long placeId, @PathVariable Long placeImageId);


}
