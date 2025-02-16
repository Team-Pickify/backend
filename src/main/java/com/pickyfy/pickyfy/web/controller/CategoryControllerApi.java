package com.pickyfy.pickyfy.web.controller;

import com.pickyfy.pickyfy.web.apiResponse.common.ApiResponse;
import com.pickyfy.pickyfy.web.dto.request.CategoryTypeRequest;
import com.pickyfy.pickyfy.web.dto.response.CategoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@Tag(name = "카테고리")
public interface CategoryControllerApi {

    @Operation(summary = "카테고리 생성 API", description = "관리자용 카테고리 생성 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @PostMapping("/admin/category")
    ApiResponse<Long> createCategory(@Valid @RequestBody CategoryTypeRequest request);

    @Operation(summary = "카테고리 조회 API", description = "특정 카테고리를 조회하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @GetMapping("/category/{id}")
    ApiResponse<CategoryResponse> getCategory(@PathVariable Long id);

    @Operation(summary = "전체 카테고리 조회 API", description = "모든 카테고리를 조회하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @GetMapping("/categories")
    ApiResponse<List<CategoryResponse>> getAllCategories();

    @Operation(summary = "카테고리 수정 API", description = "관리자용 카테고리 수정 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @PutMapping("/admin/category/{id}")
    ApiResponse<Long> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryTypeRequest request);

    @Operation(summary = "카테고리 삭제 API", description = "관리자용 카테고리 삭제 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @DeleteMapping("/admin/category/{id}")
    ApiResponse<Long> deleteCategory(@PathVariable Long id);
}