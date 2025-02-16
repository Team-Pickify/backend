package com.pickyfy.pickyfy.web.controller;

import com.pickyfy.pickyfy.web.apiResponse.common.ApiResponse;
import com.pickyfy.pickyfy.service.CategoryService;
import com.pickyfy.pickyfy.web.apiResponse.success.SuccessStatus;
import com.pickyfy.pickyfy.web.dto.request.CategoryTypeRequest;
import com.pickyfy.pickyfy.web.dto.response.CategoryResponse;
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
@RequestMapping("")
@RequiredArgsConstructor
public class CategoryController implements CategoryControllerApi{
    private final CategoryService categoryService;

    @PostMapping("/admin/category")
    public ApiResponse<Long> createCategory(@Valid @RequestBody CategoryTypeRequest request) {
        Long categoryId = categoryService.createCategory(request);
        return ApiResponse.onSuccess(SuccessStatus.ADD_CATEGORY_SUCCESS, categoryId);
    }

    @GetMapping("/category/{id}")
    public ApiResponse<CategoryResponse> getCategory(@PathVariable Long id) {
        CategoryResponse response = categoryService.getCategory(id);
        return ApiResponse.onSuccess(SuccessStatus.CATEGORIES_RETRIEVED, response);
    }

    @GetMapping("/categories")
    public ApiResponse<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> responses = categoryService.getAllCategories();
        return ApiResponse.onSuccess(SuccessStatus.CATEGORIES_RETRIEVED, responses);
    }

    @PutMapping("/admin/category/{id}")
    public ApiResponse<Long> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryTypeRequest request) {
        categoryService.updateCategory(id, request);
        return ApiResponse.onSuccess(SuccessStatus.EDIT_CATEGORY_SUCCESS, id);
    }

    @DeleteMapping("/admin/category/{id}")
    public ApiResponse<Long> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ApiResponse.onSuccess(SuccessStatus.DELETE_CATEGORY_SUCCESS, id);
    }
}
