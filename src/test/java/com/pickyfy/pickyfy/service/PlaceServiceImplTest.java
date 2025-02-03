package com.pickyfy.pickyfy.service;

import static org.junit.jupiter.api.Assertions.*;

import com.pickyfy.pickyfy.domain.Place;
import com.pickyfy.pickyfy.repository.PlaceRepository;
import com.pickyfy.pickyfy.web.dto.NearbyPlaceSearchCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

    @InjectMocks
    private PlaceServiceImpl placeService;

    @Mock
    private PlaceRepository placeRepository;

    private Place place1;
    private Place place2;

    @BeforeEach
    void setUp() {
        // 테스트에 사용할 Place 객체들을 미리 생성합니다
        place1 = Place.builder()
                .name("카페1")
                .shortDescription("테스트 카페1")
                .latitude(new BigDecimal("37.5665"))
                .longitude(new BigDecimal("126.9780"))
                .address("서울시 중구")
                .build();

        place2 = Place.builder()
                .name("카페2")
                .shortDescription("테스트 카페2")
                .latitude(new BigDecimal("37.5668"))
                .longitude(new BigDecimal("126.9783"))
                .address("서울시 중구")
                .build();
    }

    @Test
    @DisplayName("주변 장소 검색 - 정상 케이스")
    void searchNearbyPlaces_Success() {
        // given
        BigDecimal lat = new BigDecimal("37.5665");
        BigDecimal lon = new BigDecimal("126.9780");
        Double distance = 1000.0;
        List<Long> categories = Arrays.asList(1L, 2L);
        List<Long> magazines = Arrays.asList(1L);

        List<Place> expectedPlaces = Arrays.asList(place1, place2);

        // PlaceRepository의 동작을 모킹합니다
        given(placeRepository.searchNearbyPlaces(any(NearbyPlaceSearchCondition.class)))
                .willReturn(expectedPlaces);

        // when
        List<Place> result = placeService.searchNearbyPlaces(lat, lon, distance, categories, magazines);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).contains(place1, place2);

        // 리포지토리 메소드가 적절한 파라미터로 호출되었는지 확인
        verify(placeRepository).searchNearbyPlaces(any(NearbyPlaceSearchCondition.class));
    }

    @Test
    @DisplayName("주변 장소 검색 - 카테고리/매거진 필터 없이 검색")
    void searchNearbyPlaces_WithoutFilters() {
        // given
        BigDecimal lat = new BigDecimal("37.5665");
        BigDecimal lon = new BigDecimal("126.9780");
        Double distance = 1000.0;
        List<Long> categories = null;
        List<Long> magazines = null;

        List<Place> expectedPlaces = Arrays.asList(place1, place2);

        given(placeRepository.searchNearbyPlaces(any(NearbyPlaceSearchCondition.class)))
                .willReturn(expectedPlaces);

        // when
        List<Place> result = placeService.searchNearbyPlaces(lat, lon, distance, categories, magazines);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        verify(placeRepository).searchNearbyPlaces(any(NearbyPlaceSearchCondition.class));
    }

    @Test
    @DisplayName("주변 장소 검색 - 검색 결과 없음")
    void searchNearbyPlaces_NoResults() {
        // given
        BigDecimal lat = new BigDecimal("37.5665");
        BigDecimal lon = new BigDecimal("126.9780");
        Double distance = 1000.0;
        List<Long> categories = Arrays.asList(1L);
        List<Long> magazines = Arrays.asList(1L);

        given(placeRepository.searchNearbyPlaces(any(NearbyPlaceSearchCondition.class)))
                .willReturn(List.of());

        // when
        List<Place> result = placeService.searchNearbyPlaces(lat, lon, distance, categories, magazines);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }
}