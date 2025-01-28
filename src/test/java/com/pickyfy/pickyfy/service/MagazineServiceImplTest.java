package com.pickyfy.pickyfy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.pickyfy.pickyfy.domain.Magazine;
import com.pickyfy.pickyfy.repository.MagazineRepository;
import com.pickyfy.pickyfy.web.dto.request.MagazineCreateRequest;
import com.pickyfy.pickyfy.web.dto.request.MagazineUpdateRequest;
import com.pickyfy.pickyfy.web.dto.response.MagazineResponse;
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
class MagazineServiceImplTest {

    @Mock
    private MagazineRepository magazineRepository;

    @InjectMocks
    private MagazineServiceImpl magazineService;

    private Magazine magazine;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        magazine = Magazine.builder()
                .title("테스트 매거진")
                .iconUrl("test-icon.png")
                .content("테스트 내용입니다.")
                .build();

        // Reflection을 사용해 id 설정
        Field idField = Magazine.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(magazine, 1L);
    }

    @Test
    void createMagazine_Success() {
        // Given
        MagazineCreateRequest request = new MagazineCreateRequest(
                "테스트 매거진",
                "test-icon.png",
                "테스트 내용입니다."
        );
        given(magazineRepository.save(any(Magazine.class))).willReturn(magazine);

        // When
        Long magazineId = magazineService.createMagazine(request);

        // Then
        assertThat(magazineId).isEqualTo(1L);
        verify(magazineRepository).save(any(Magazine.class));
    }

    @Test
    void getMagazine_Success() {
        // Given
        given(magazineRepository.findById(1L)).willReturn(Optional.of(magazine));

        // When
        MagazineResponse response = magazineService.getMagazine(1L);

        // Then
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("테스트 매거진");
        assertThat(response.iconUrl()).isEqualTo("test-icon.png");
        assertThat(response.content()).isEqualTo("테스트 내용입니다.");
    }

    @Test
    void getMagazine_NotFound_ThrowsException() {
        // Given
        given(magazineRepository.findById(1L)).willReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> magazineService.getMagazine(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(String.valueOf(1L));
    }

    @Test
    void getAllMagazines_Success() {
        // Given
        List<Magazine> magazines = List.of(magazine);
        given(magazineRepository.findAll()).willReturn(magazines);

        // When
        List<MagazineResponse> responses = magazineService.getAllMagazines();

        // Then
        assertThat(responses).hasSize(1);
        MagazineResponse response = responses.get(0);
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("테스트 매거진");
        assertThat(response.iconUrl()).isEqualTo("test-icon.png");
        assertThat(response.content()).isEqualTo("테스트 내용입니다.");
    }

    @Test
    void updateMagazine_Success() {
        // Given
        MagazineUpdateRequest request = new MagazineUpdateRequest(
                "수정된 매거진",
                "updated-icon.png",
                "수정된 내용입니다."
        );
        given(magazineRepository.findById(1L)).willReturn(Optional.of(magazine));

        // When
        magazineService.updateMagazine(1L, request);

        // Then
        assertThat(magazine.getTitle()).isEqualTo("수정된 매거진");
        assertThat(magazine.getIconUrl()).isEqualTo("updated-icon.png");
        assertThat(magazine.getContent()).isEqualTo("수정된 내용입니다.");
    }

    @Test
    void updateMagazine_NotFound_ThrowsException() {
        // Given
        MagazineUpdateRequest request = new MagazineUpdateRequest(
                "수정된 매거진",
                "updated-icon.png",
                "수정된 내용입니다."
        );
        given(magazineRepository.findById(1L)).willReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> magazineService.updateMagazine(1L, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(String.valueOf(1L));
    }

    @Test
    void deleteMagazine_Success() {
        // Given
        given(magazineRepository.findById(1L)).willReturn(Optional.of(magazine));

        // When
        magazineService.deleteMagazine(1L);

        // Then
        verify(magazineRepository).delete(magazine);
    }

    @Test
    void deleteMagazine_NotFound_ThrowsException() {
        // Given
        given(magazineRepository.findById(1L)).willReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> magazineService.deleteMagazine(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(String.valueOf(1L));
    }
}