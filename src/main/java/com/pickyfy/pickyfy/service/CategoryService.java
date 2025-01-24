package com.pickyfy.pickyfy.service;

import com.pickyfy.pickyfy.domain.Category;
import com.pickyfy.pickyfy.web.dto.request.CategoryCreateRequest;
import com.pickyfy.pickyfy.web.dto.request.CategoryUpdateRequest;
import com.pickyfy.pickyfy.web.dto.response.CategoryResponse;
import java.util.List;

public interface CategoryService {
    public Long createCategory(CategoryCreateRequest categoryCreateRequest);
    public CategoryResponse getCategory(Long id);
    public List<CategoryResponse> getAllCategories();
    public void updateCategory(Long id, CategoryUpdateRequest request);
    public void deleteCategory(Long id);
}
