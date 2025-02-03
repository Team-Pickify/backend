package com.pickyfy.pickyfy.service;

import static org.junit.jupiter.api.Assertions.*;

import com.pickyfy.pickyfy.domain.Category;
import com.pickyfy.pickyfy.domain.CategoryType;
import com.pickyfy.pickyfy.domain.Magazine;
import com.pickyfy.pickyfy.domain.Place;
import com.pickyfy.pickyfy.domain.PlaceCategory;
import com.pickyfy.pickyfy.domain.PlaceMagazine;
import com.pickyfy.pickyfy.repository.CategoryRepository;
import com.pickyfy.pickyfy.repository.MagazineRepository;
import com.pickyfy.pickyfy.repository.PlaceRepository;
import com.pickyfy.pickyfy.web.dto.NearbyPlaceSearchCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
class PlaceServiceImplTest {

    @Autowired
    private PlaceService placeService;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MagazineRepository magazineRepository;

    @Test
    @DisplayName("주어진 반경 내의 장소를 검색하고 카테고리와 매거진으로 필터링한다")
    void searchNearbyPlaces() {
        // given
        // 카테고리와 매거진 데이터 준비
        Category cafe = categoryRepository.save(Category.builder().type(CategoryType.BAR_PUB).build());
        Category restaurant = categoryRepository.save(Category.builder().type(CategoryType.RESTAURANT).build());

        Magazine hotPlace = magazineRepository.save(Magazine.builder().title("핫플레이스").content("핫플").build());
        Magazine datePlace = magazineRepository.save(Magazine.builder().title("데이트코스").content("데코").build());

        // 중심점 근처에 테스트용 장소들을 생성
        Place centerPlace = createPlace("중심점", "37.5665", "126.9780",
                List.of(cafe), List.of(hotPlace));

        Place nearbyPlace = createPlace("300m 거리의 카페", "37.5665", "126.9810",
                List.of(cafe), List.of(datePlace));

        Place farPlace = createPlace("1km 거리의 식당", "37.5755", "126.9780",
                List.of(restaurant), List.of(hotPlace));

        placeRepository.saveAll(Arrays.asList(centerPlace, nearbyPlace, farPlace));

        // when
        // 500m 반경 내의 카페 카테고리에 속한 장소 검색
        List<Place> result = placeService.searchNearbyPlaces(
                new BigDecimal("37.5665"),  // 중심점 위도
                new BigDecimal("126.9780"), // 중심점 경도
                500.0,                      // 500m 반경
                List.of(cafe.getId()),      // 카페 카테고리만 검색
                List.of(hotPlace.getId(), datePlace.getId())  // 모든 매거진 포함
        );

        // then
        assertThat(result).hasSize(2)  // 중심점과 300m 거리의 카페만 포함되어야 함
                .extracting("name")
                .containsExactlyInAnyOrder("중심점", "300m 거리의 카페");

        // 1km 거리의 식당은 반경을 벗어나므로 포함되지 않아야 함
        assertThat(result).extracting("name")
                .doesNotContain("1km 거리의 식당");
    }

    @Test
    @DisplayName("카테고리와 매거진 필터가 없을 경우 반경 내의 모든 장소를 검색한다")
    void searchNearbyPlaces_NoFilters() {
        // given
        // 카테고리와 매거진 데이터 준비
        Category cafe = categoryRepository.save(Category.builder().type(CategoryType.BAR_PUB).build());

        Magazine datePlace = magazineRepository.save(Magazine.builder().title("데이트코스").content("데코").build());

        Place centerPlace = createPlace("중심점", "37.5665", "126.9780", List.of(cafe), List.of(datePlace));
        Place nearbyPlace = createPlace("300m 거리", "37.5665", "126.9810", List.of(cafe), List.of(datePlace));

        placeRepository.saveAll(Arrays.asList(centerPlace, nearbyPlace));

        // when
        List<Place> result = placeService.searchNearbyPlaces(
                new BigDecimal("37.5665"),
                new BigDecimal("126.9780"),
                500.0,
                null,    // 카테고리 필터 없음
                null     // 매거진 필터 없음
        );

        // then
        assertThat(result).hasSize(2)
                .extracting("name")
                .containsExactlyInAnyOrder("중심점", "300m 거리");
    }

    @Test
    @DisplayName("카테고리와 매거진 필터링 테스트")
    void searchNearbyPlaces_CategoryAndMagazine() {
        // given
        // 카테고리와 매거진 데이터 준비
        Category cafe = categoryRepository.save(Category.builder().type(CategoryType.BAR_PUB).build());
        Category restaurant = categoryRepository.save(Category.builder().type(CategoryType.RESTAURANT).build());

        Magazine hotPlace = magazineRepository.save(Magazine.builder().title("핫플레이스").content("핫플").build());
        Magazine datePlace = magazineRepository.save(Magazine.builder().title("데이트코스").content("데코").build());

        // 중심점 근처에 테스트용 장소들을 생성
        Place centerPlace = createPlace("중심점1", "37.5665", "126.9780",
                List.of(cafe), List.of(hotPlace));

        Place nearbyPlace = createPlace("중심점2", "37.5665", "126.9780",
                List.of(cafe), List.of(datePlace));

        Place farPlace = createPlace("중심점3", "37.5665", "126.9780",
                List.of(restaurant), List.of(hotPlace));

        placeRepository.saveAll(Arrays.asList(centerPlace, nearbyPlace, farPlace));

        // when
        // 500m 반경 내의 카페 카테고리에 속한 장소 검색
        List<Place> result = placeService.searchNearbyPlaces(
                new BigDecimal("37.5665"),  // 중심점 위도
                new BigDecimal("126.9780"), // 중심점 경도
                500.0,                      // 500m 반경
                List.of(cafe.getId()),      // 카페 카테고리만 검색
                List.of(hotPlace.getId(), datePlace.getId())  // 모든 매거진 포함
        );

        // then
        assertThat(result).hasSize(2)  // 카페만 포함되어야 함
                .extracting("name")
                .containsExactlyInAnyOrder("중심점1", "중심점2");

        // 중심점3은 restaurant 이므로 포함되지 않아야 함
        assertThat(result).extracting("name")
                .doesNotContain("중심점3");
    }

    @Test
    @DisplayName("잘못된 위도/경도 값으로 검색시 빈 결과를 반환한다")
    void searchNearbyPlaces_InvalidLocation() {
        // given
        Place centerPlace = createPlace("중심점", "37.5665", "126.9780", null, null);
        placeRepository.save(centerPlace);

        // when
        List<Place> result = placeService.searchNearbyPlaces(
                new BigDecimal("999.9999"),  // 잘못된 위도
                new BigDecimal("999.9999"),  // 잘못된 경도
                500.0,
                null,
                null
        );

        // then
        assertThat(result).isEmpty();
    }

    // 테스트 헬퍼 메서드
    private Place createPlace(String name, String lat, String lon,
                              List<Category> categories, List<Magazine> magazines) {
        Place place = Place.builder()
                .name(name)
                .shortDescription("설명")
                .address("주소")
                .latitude(new BigDecimal(lat))
                .longitude(new BigDecimal(lon))
                .build();

        // 카테고리가 있는 경우 연관관계 설정
        if (categories != null) {
            categories.forEach(category -> {
                PlaceCategory placeCategory = PlaceCategory.builder()
                        .place(place)
                        .category(category)
                        .build();
                place.getPlaceCategories().add(placeCategory);
            });
        }

        // 매거진이 있는 경우 연관관계 설정
        if (magazines != null) {
            magazines.forEach(magazine -> {
                PlaceMagazine placeMagazine = PlaceMagazine.builder()
                        .place(place)
                        .magazine(magazine)
                        .build();
                place.getPlaceMagazines().add(placeMagazine);
            });
        }

        return place;
    }
}