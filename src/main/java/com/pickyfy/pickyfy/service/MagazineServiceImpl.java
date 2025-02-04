package com.pickyfy.pickyfy.service;

import com.pickyfy.pickyfy.apiPayload.code.status.ErrorStatus;
import com.pickyfy.pickyfy.domain.Magazine;
import com.pickyfy.pickyfy.exception.DuplicateResourceException;
import com.pickyfy.pickyfy.repository.MagazineRepository;
import com.pickyfy.pickyfy.web.dto.request.MagazineCreateRequest;
import com.pickyfy.pickyfy.web.dto.request.MagazineUpdateRequest;
import com.pickyfy.pickyfy.web.dto.response.MagazineResponse;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MagazineServiceImpl implements MagazineService {

    private final MagazineRepository magazineRepository;

    @Override
    @Transactional
    public Long createMagazine(MagazineCreateRequest request) {
        validateDuplicateTitle(request.title());
        Magazine magazine = Magazine.builder()
                .title(request.title())
                .iconUrl(request.iconUrl())
                .build();

        return magazineRepository.save(magazine).getId();
    }

    @Override
    public MagazineResponse getMagazine(Long id) {
        Magazine magazine = findMagazineById(id);
        return MagazineResponse.from(magazine);
    }

    @Override
    public List<MagazineResponse> getAllMagazines() {
        return magazineRepository.findAll().stream()
                .map(MagazineResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateMagazine(Long id, MagazineUpdateRequest request) {
        Magazine magazine = findMagazineById(id);
        magazine.update(request.title(), request.iconUrl());
    }

    @Override
    @Transactional
    public void deleteMagazine(Long id) {
        Magazine magazine = findMagazineById(id);
        magazineRepository.delete(magazine);
    }

    private Magazine findMagazineById(Long id) {
        return magazineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        ErrorStatus.MAGAZINE_NOT_FOUND.getMessage() + ": " + id));
    }

    private void validateDuplicateTitle(String title) {
        if (magazineRepository.existsByTitle(title)) {
            throw new DuplicateResourceException(ErrorStatus.CATEGORY_DUPLICATED);
        }
    }
}