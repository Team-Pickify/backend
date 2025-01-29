package com.pickyfy.pickyfy.web.controller;

import com.pickyfy.pickyfy.apiPayload.ApiResponse;
import com.pickyfy.pickyfy.web.dto.request.CategoryCreateRequest;
import com.pickyfy.pickyfy.web.dto.request.PlaceCreateRequest;
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
@Tag(name = "관리자")
public interface AdminControllerApi {

    @Operation(summary = "Place 생성", description = "Place 를 생성하고 최대 5장의 PlaceImage 를 업로드합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @PostMapping(value = "/places", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<Long> createPlace(@Parameter(description = "장소 생성 요청 정보", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                                  @RequestPart("request") @Valid PlaceCreateRequest request,

                                  @Parameter(description = "장소 이미지 리스트 (최대 5개)", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
                                  @RequestPart(value = "image", required = false) List<MultipartFile> image);

    @Operation(summary = "Place 수정", description = "Place 를 수정하고 5장의 PlaceImage 를 수정하여 업로드합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @PatchMapping(value = "/places/{placeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<Long> updatePlace(
            @PathVariable Long placeId,
            @RequestPart("request") @Valid PlaceCreateRequest request,
            @RequestPart(value = "image", required = false) List<MultipartFile> image);

    @Operation(summary = "Place 삭제", description = "Place 와 PlaceImage 모두 삭제 합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @DeleteMapping("/places/{placeId}")
    ApiResponse<Void> deletePlace(@PathVariable Long placeId);

}
