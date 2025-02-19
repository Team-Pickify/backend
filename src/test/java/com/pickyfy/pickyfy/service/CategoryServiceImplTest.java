package com.pickyfy.pickyfy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.pickyfy.pickyfy.domain.Category;
import com.pickyfy.pickyfy.domain.CategoryType;
import com.pickyfy.pickyfy.exception.DuplicateResourceException;
import com.pickyfy.pickyfy.repository.CategoryRepository;
import com.pickyfy.pickyfy.web.dto.request.CategoryTypeRequest;
import com.pickyfy.pickyfy.web.dto.response.CategoryResponse;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
class CategoryServiceImplTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;

    @Test
    void createCategory_DuplicateType_ThrowsException() {
        // Given
        CategoryTypeRequest request = new CategoryTypeRequest(CategoryType.CAFE_BAKERY);

        // When/Then
        assertThatThrownBy(() -> categoryService.createCategory(request))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void getCategory_Success() {
        // When
        CategoryResponse response = categoryService.getCategory(1L);

        // Then
        assertThat(response.name()).isEqualTo(CategoryType.ALL.getDisplayName());
    }

    @Test
    void getCategory_NotFound_ThrowsException() {
        // Given
        Long nonExistentId = 999L;

        // When/Then
        assertThatThrownBy(() -> categoryService.getCategory(nonExistentId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getAllCategories_Success() {
        // When
        List<CategoryResponse> responses = categoryService.getAllCategories();

        // Then
        assertThat(responses).hasSize(7);
        assertThat(responses.getFirst().id()).isEqualTo(1L);
    }

    @Test
    void updateCategory_DuplicateType_ThrowsException() {
        // Given
        Category anotherCategory = Category.builder()
                .type(CategoryType.RESTAURANT)
                .build();
        categoryRepository.save(anotherCategory);

        CategoryTypeRequest request = new CategoryTypeRequest(CategoryType.RESTAURANT);

        // When/Then
        assertThatThrownBy(() -> categoryService.updateCategory(1L, request))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void deleteCategory_Success() {
        // When
        categoryService.deleteCategory(1L);

        // Then
        assertThat(categoryRepository.findById(1L)).isEmpty();
    }

    @Test
    void deleteCategory_NotFound_ThrowsException() {
        // Given
        Long nonExistentId = 999L;

        // When/Then
        assertThatThrownBy(() -> categoryService.deleteCategory(nonExistentId))
                .isInstanceOf(EntityNotFoundException.class);
    }
}