package com.pickyfy.pickyfy.service;

import com.pickyfy.pickyfy.web.apiResponse.error.ErrorStatus;
import com.pickyfy.pickyfy.domain.Magazine;
import com.pickyfy.pickyfy.exception.DuplicateResourceException;
import com.pickyfy.pickyfy.repository.MagazineRepository;
import com.pickyfy.pickyfy.web.dto.request.MagazineRequest;
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
    private final S3Service s3Service;

    @Override
    @Transactional
    public Long createMagazine(MagazineRequest request) {
        validateDuplicateTitle(request.title());
        Magazine magazine = Magazine.builder()
                .title(request.title())
                .iconUrl(s3Service.upload(request.iconFile()))
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
    public void updateMagazine(Long id, MagazineRequest request) {
        Magazine magazine = findMagazineById(id);
        if (request.iconFile() == null){
            magazine.update(request.title());
            return;
        }
        String oldIconUrl = magazine.getIconUrl();
        magazine.update(request.title(), s3Service.upload(request.iconFile()));
        if (oldIconUrl != null && !oldIconUrl.isEmpty()) {
            s3Service.removeFile(oldIconUrl);
        }
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