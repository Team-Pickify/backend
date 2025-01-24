package com.pickyfy.pickyfy.repository;

import com.pickyfy.pickyfy.domain.Category;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByName(String name);
    Optional<Category> findById(Long id);
    boolean existsByName(String name);
}
