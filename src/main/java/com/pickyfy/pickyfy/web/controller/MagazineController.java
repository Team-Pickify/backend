package com.pickyfy.pickyfy.web.controller;

import com.pickyfy.pickyfy.apiPayload.ApiResponse;
import com.pickyfy.pickyfy.service.MagazineService;
import com.pickyfy.pickyfy.web.dto.request.MagazineCreateRequest;
import com.pickyfy.pickyfy.web.dto.request.MagazineUpdateRequest;
import com.pickyfy.pickyfy.web.dto.response.MagazineResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class MagazineController implements MagazineControllerApi {

    private final MagazineService magazineService;

    @PostMapping("/admin/magazines")
    public ApiResponse<Long> createMagazine(@Valid @RequestBody MagazineCreateRequest request) {
        Long magazineId = magazineService.createMagazine(request);
        return ApiResponse.onSuccess(magazineId);
    }

    @GetMapping("/magazines/{id}")
    public ApiResponse<MagazineResponse> getMagazine(@PathVariable Long id) {
        MagazineResponse response = magazineService.getMagazine(id);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/magazines")
    public ApiResponse<List<MagazineResponse>> getAllMagazines() {
        List<MagazineResponse> responses = magazineService.getAllMagazines();
        return ApiResponse.onSuccess(responses);
    }

    @PutMapping("/admin/magazines/{id}")
    public ApiResponse<Long> updateMagazine(
            @PathVariable Long id,
            @Valid @RequestBody MagazineUpdateRequest request) {
        magazineService.updateMagazine(id, request);
        return ApiResponse.onSuccess(id);
    }

    @DeleteMapping("/admin/magazines/{id}")
    public ApiResponse<Long> deleteMagazine(@PathVariable Long id) {
        magazineService.deleteMagazine(id);
        return ApiResponse.onSuccess(id);
    }
}
