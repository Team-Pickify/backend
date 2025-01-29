package com.pickyfy.pickyfy.web.controller;

import com.pickyfy.pickyfy.apiPayload.ApiResponse;
import com.pickyfy.pickyfy.service.AdminService;
import com.pickyfy.pickyfy.service.AdminServiceImpl;
import com.pickyfy.pickyfy.web.dto.request.CategoryUpdateRequest;
import com.pickyfy.pickyfy.web.dto.request.PlaceCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController implements AdminControllerApi {

    private final AdminServiceImpl adminService;

    @Operation(summary = "장소 생성", description = "장소를 생성하고 최대 5장의 이미지를 업로드합니다.")
    @PostMapping(value = "/place", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Long> createPlace(
            @Parameter(description = "장소 생성 요청 정보", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @RequestPart("request") @Valid PlaceCreateRequest request,

            @Parameter(description = "장소 이미지 리스트 (최대 5개)", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestPart(value = "image", required = false) List<MultipartFile> image) {

        Long id = adminService.createPlace(request, image);
        return ApiResponse.onSuccess(id);
    }
}


