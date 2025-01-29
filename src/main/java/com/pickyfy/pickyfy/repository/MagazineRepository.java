package com.pickyfy.pickyfy.repository;

import com.pickyfy.pickyfy.domain.Magazine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MagazineRepository extends JpaRepository<Magazine, Long> {
    boolean existsByTitle(String title);
}
