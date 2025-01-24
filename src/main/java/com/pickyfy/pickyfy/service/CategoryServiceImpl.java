package com.pickyfy.pickyfy.service;

import com.pickyfy.pickyfy.apiPayload.code.status.ErrorStatus;
import com.pickyfy.pickyfy.domain.Category;
import com.pickyfy.pickyfy.exception.DuplicateResourceException;
import com.pickyfy.pickyfy.repository.CategoryRepository;
import com.pickyfy.pickyfy.web.dto.request.CategoryCreateRequest;
import com.pickyfy.pickyfy.web.dto.request.CategoryUpdateRequest;
import com.pickyfy.pickyfy.web.dto.response.CategoryResponse;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Long createCategory(CategoryCreateRequest categoryCreateRequest) {
        validateDuplicateName(categoryCreateRequest.name());

        Category category = Category.builder()
                .name(categoryCreateRequest.name())
                .build();

        return categoryRepository.save(category).getId();
    }

    @Override
    public CategoryResponse getCategory(Long id) {
        Category category = findCategoryById(id);
        return CategoryResponse.from(category);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateCategory(Long id, CategoryUpdateRequest request) {
        Category category = findCategoryById(id);

        if (!category.getName().equals(request.name())) {
            validateDuplicateName(request.name());
        }

        category.update(request.name());
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = findCategoryById(id);
        categoryRepository.delete(category);
    }

    private Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorStatus.CATEGORY_NOT_FOUND.getMessage() + ": " + id));
    }

    private void validateDuplicateName(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new DuplicateResourceException(ErrorStatus.CATEGORY_DUPLICATED);
        }
    }
}
