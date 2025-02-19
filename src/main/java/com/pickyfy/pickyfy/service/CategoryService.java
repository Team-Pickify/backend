package com.pickyfy.pickyfy.service;

import com.pickyfy.pickyfy.web.dto.request.CategoryTypeRequest;
import com.pickyfy.pickyfy.web.dto.response.CategoryResponse;
import java.util.List;

public interface CategoryService {
    Long createCategory(CategoryTypeRequest categoryCreateRequest);
    CategoryResponse getCategory(Long id);
    List<CategoryResponse> getAllCategories();
    void updateCategory(Long id, CategoryTypeRequest request);
    void deleteCategory(Long id);
}
