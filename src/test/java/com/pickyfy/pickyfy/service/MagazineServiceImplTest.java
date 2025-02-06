package com.pickyfy.pickyfy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class MagazineServiceImplTest {

    @Mock
    private MagazineRepository magazineRepository;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private MagazineServiceImpl magazineService;

    @Captor
    private ArgumentCaptor<Magazine> magazineArgumentCaptor;

    private Magazine magazine;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        magazine = Magazine.builder()
                .title("테스트 매거진")
                .iconUrl("test-icon.png")
                .build();

        // Reflection을 사용해 id 설정
        Field idField = Magazine.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(magazine, 1L);
    }

    @Test
    void createMagazine_Success() {
        // Given
        // MockMultipartFile 생성 - 실제 파일 처리는 하지 않고 테스트용 객체만 생성
        MultipartFile mockFile = new MockMultipartFile(
                "iconFile",           // 폼 필드 이름
                "test-icon.png",      // 원본 파일 이름
                "image/png",          // 컨텐트 타입
                "test".getBytes()     // 간단한 테스트 데이터
        );

        MagazineCreateRequest request = new MagazineCreateRequest(
                "테스트 매거진",
                mockFile
        );

        given(magazineRepository.save(any(Magazine.class))).willReturn(magazine);

        // When
        Long magazineId = magazineService.createMagazine(request);

        // Then
        assertThat(magazineId).isEqualTo(1L);
        verify(magazineRepository).save(any(Magazine.class));
    }

    @Test
    void updateMagazine_SuccessWithJustTitle() {
        // Given
        Magazine existingMagazine = new Magazine("기존 매거진", "test-icon.png");
        MagazineUpdateRequest request = new MagazineUpdateRequest(
                "수정된 매거진",
                null
        );

        when(magazineRepository.findById(1L)).thenReturn(Optional.of(existingMagazine));

        // When
        magazineService.updateMagazine(1L, request);

        // Then
        verify(magazineRepository).findById(1L);

        assertThat(existingMagazine.getTitle()).isEqualTo("수정된 매거진");
        assertThat(existingMagazine.getIconUrl()).isEqualTo("test-icon.png");
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
        MagazineResponse response = responses.getFirst();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("테스트 매거진");
        assertThat(response.iconUrl()).isEqualTo("test-icon.png");
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