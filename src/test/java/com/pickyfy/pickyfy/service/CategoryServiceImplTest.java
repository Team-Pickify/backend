package com.pickyfy.pickyfy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.pickyfy.pickyfy.domain.Category;
import com.pickyfy.pickyfy.exception.DuplicateResourceException;
import com.pickyfy.pickyfy.repository.CategoryRepository;
import com.pickyfy.pickyfy.web.dto.request.CategoryCreateRequest;
import com.pickyfy.pickyfy.web.dto.request.CategoryUpdateRequest;
import com.pickyfy.pickyfy.web.dto.response.CategoryResponse;
import jakarta.persistence.EntityNotFoundException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        category = Category.builder()
                .name("Test Category")
                .build();

        // Reflection을 사용해 id 설정
        Field idField = Category.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(category, 1L);
    }

    @Test
    void createCategory_Success() {
        // Given
        CategoryCreateRequest request = new CategoryCreateRequest("Test Category");
        given(categoryRepository.existsByName(request.name())).willReturn(false);
        given(categoryRepository.save(any(Category.class))).willReturn(category);

        // When
        Long categoryId = categoryService.createCategory(request);

        // Then
        assertThat(categoryId).isEqualTo(1L);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createCategory_DuplicateName_ThrowsException() {
        // Given
        CategoryCreateRequest request = new CategoryCreateRequest("Test Category");
        given(categoryRepository.existsByName(request.name())).willReturn(true);

        // When/Then
        assertThatThrownBy(() -> categoryService.createCategory(request))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void getCategory_Success() {
        // Given
        given(categoryRepository.findById(1L)).willReturn(Optional.of(category));

        // When
        CategoryResponse response = categoryService.getCategory(1L);

        // Then
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Test Category");
    }

    @Test
    void getCategory_NotFound_ThrowsException() {
        // Given
        given(categoryRepository.findById(1L)).willReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> categoryService.getCategory(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getAllCategories_Success() {
        // Given
        List<Category> categories = List.of(category);
        given(categoryRepository.findAll()).willReturn(categories);

        // When
        List<CategoryResponse> responses = categoryService.getAllCategories();

        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).id()).isEqualTo(1L);
        assertThat(responses.get(0).name()).isEqualTo("Test Category");
    }

    @Test
    void updateCategory_Success() {
        // Given
        CategoryUpdateRequest request = new CategoryUpdateRequest("Updated Category");
        given(categoryRepository.findById(1L)).willReturn(Optional.of(category));
        given(categoryRepository.existsByName(request.name())).willReturn(false);

        // When
        categoryService.updateCategory(1L, request);

        // Then
        assertThat(category.getName()).isEqualTo("Updated Category");
    }

    @Test
    void updateCategory_DuplicateName_ThrowsException() {
        // Given
        CategoryUpdateRequest request = new CategoryUpdateRequest("Updated Category");
        given(categoryRepository.findById(1L)).willReturn(Optional.of(category));
        given(categoryRepository.existsByName(request.name())).willReturn(true);

        // When/Then
        assertThatThrownBy(() -> categoryService.updateCategory(1L, request))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void deleteCategory_Success() {
        // Given
        given(categoryRepository.findById(1L)).willReturn(Optional.of(category));

        // When
        categoryService.deleteCategory(1L);

        // Then
        verify(categoryRepository).delete(category);
    }

    @Test
    void deleteCategory_NotFound_ThrowsException() {
        // Given
        given(categoryRepository.findById(1L)).willReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> categoryService.deleteCategory(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}