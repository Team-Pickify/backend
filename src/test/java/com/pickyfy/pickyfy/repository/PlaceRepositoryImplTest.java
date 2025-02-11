package com.pickyfy.pickyfy.repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.pickyfy.pickyfy.domain.Category;
import com.pickyfy.pickyfy.domain.CategoryType;
import com.pickyfy.pickyfy.domain.Magazine;
import com.pickyfy.pickyfy.domain.Place;
import com.pickyfy.pickyfy.domain.PlaceCategory;
import com.pickyfy.pickyfy.domain.PlaceMagazine;
import com.pickyfy.pickyfy.web.dto.NearbyPlaceSearchCondition;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class PlaceRepositoryImplTest {

    @Autowired
    private PlaceRepositoryImpl placeRepository;

    @Autowired
    private EntityManager em;

    private Category category1;
    private Category category2;
    private Magazine magazine1;
    private Magazine magazine2;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 설정
        category1 = createCategory(CategoryType.RESTAURANT);
        category2 = createCategory(CategoryType.CAFE_BAKERY);

        magazine1 = createMagazine("맛집 매거진");
        magazine2 = createMagazine("카페 매거진");

        // 서울 강남역 근처의 좌표
        Place place1 = createPlace("테스트 장소 1",
                new BigDecimal("37.498095"),
                new BigDecimal("127.027610"));

        // 2km 떨어진 위치
        Place place2 = createPlace("테스트 장소 2",
                new BigDecimal("37.514322"),
                new BigDecimal("127.047768"));

        addCategoryToPlace(place1, category1);
        addMagazineToPlace(place1, magazine1);

        addCategoryToPlace(place2, category2);
        addMagazineToPlace(place2, magazine2);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("거리, 카테고리, 메거진으로 검색")
    void searchNearbyPlaces() {
        // given
        NearbyPlaceSearchCondition condition = new NearbyPlaceSearchCondition(
                new BigDecimal("37.498095"), // 강남역 위도
                new BigDecimal("127.027610"), // 강남역 경도
                500.0, // 1km 반경
                List.of(category1.getId(), category2.getId()), // 카테고리 ID
                List.of(magazine1.getId())  // 매거진 ID
        );

        // when
        List<Place> results = placeRepository.searchNearbyPlaces(condition);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("테스트 장소 1");
    }

    @Test
    @DisplayName("거리와 카테고리로 검색")
    void searchNearbyPlaces_WithinDistanceAndCategory() {
        // given
        NearbyPlaceSearchCondition condition = new NearbyPlaceSearchCondition(
                new BigDecimal("37.498095"),
                new BigDecimal("127.027610"),
                500.0, // 500m 반경
                List.of(category1.getId(), category2.getId()),
                null
        );

        // when
        List<Place> results = placeRepository.searchNearbyPlaces(condition);

        // then
        assertThat(results).hasSize(1);
    }

    @Test
    @DisplayName("거리와 매거진으로 검색")
    void searchNearbyPlaces_WithinDistanceAndMagazine() {
        // given
        NearbyPlaceSearchCondition condition = new NearbyPlaceSearchCondition(
                new BigDecimal("37.498095"),
                new BigDecimal("127.027610"),
                500.0, // 500m 반경
                null,
                List.of(magazine1.getId())
        );

        // when
        List<Place> results = placeRepository.searchNearbyPlaces(condition);

        // then
        assertThat(results).hasSize(1);
    }

    @Test
    @DisplayName("포함되지 않는 카테고리 검색")
    void searchNearbyPlaces_NotCorrectCategory() {
        // given
        NearbyPlaceSearchCondition condition = new NearbyPlaceSearchCondition(
                new BigDecimal("37.498095"),
                new BigDecimal("127.027610"),
                500.0, // 500m 반경
                List.of(999L),
                null

        );

        // when
        List<Place> results = placeRepository.searchNearbyPlaces(condition);

        // then
        assertThat(results).hasSize(0);
    }

    private Category createCategory(CategoryType type) {
        Category category = new Category(type);
        em.persist(category);
        return category;
    }

    private Magazine createMagazine(String name) {
        Magazine magazine = Magazine.builder().title(name).build();
        em.persist(magazine);
        return magazine;
    }

    private Place createPlace(String name, BigDecimal lat, BigDecimal lon) {
        Place place = Place.builder().shortDescription("설명").name(name).address("주소").latitude(lat).longitude(lon).build();
        em.persist(place);
        return place;
    }

    private void addCategoryToPlace(Place place, Category category) {
        PlaceCategory placeCategory = new PlaceCategory(place, category);
        em.persist(placeCategory);
    }

    private void addMagazineToPlace(Place place, Magazine magazine) {
        PlaceMagazine placeMagazine = new PlaceMagazine(place, magazine);
        em.persist(placeMagazine);
    }
}