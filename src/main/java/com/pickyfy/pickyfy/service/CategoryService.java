package com.pickyfy.pickyfy.service;

import com.pickyfy.pickyfy.domain.Category;
import com.pickyfy.pickyfy.dto.CategoryCreateRequest;
import com.pickyfy.pickyfy.repository.CategoryRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public Long createCategory(CategoryCreateRequest categoryCreateRequest) {
        validateDuplicateName(categoryCreateRequest.getName());

        Category category = Category.builder()
                .name(categoryCreateRequest.getName())
                .icon(categoryCreateRequest.getIcon())
                .build();

        return categoryRepository.save(category).getId();
    }

    private void validateDuplicateName(String name) {
        if (categoryRepository.existsByName(name)) {
            /*
            TODO: Custom Exception 만들어서 던지기
            throw new DuplicateResourceException("Category already exists with name: " + name);
             */
        }
    }
}
