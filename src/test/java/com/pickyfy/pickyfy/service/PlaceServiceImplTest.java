package com.pickyfy.pickyfy.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.pickyfy.pickyfy.domain.*;
import com.pickyfy.pickyfy.domain.UserSavedPlace;
import com.pickyfy.pickyfy.repository.CategoryRepository;
import com.pickyfy.pickyfy.repository.MagazineRepository;
import com.pickyfy.pickyfy.repository.PlaceCategoryRepository;
import com.pickyfy.pickyfy.repository.PlaceMagazineRepository;
import com.pickyfy.pickyfy.repository.PlaceRepository;
import com.pickyfy.pickyfy.repository.UserSavedPlaceRepository;
import com.pickyfy.pickyfy.repository.UserRepository;
import com.pickyfy.pickyfy.web.dto.response.PlaceSearchResponse;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class PlaceServiceImplTest {

    @Autowired
    private PlaceService placeService;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private SavedPlaceRepository savedPlaceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MagazineRepository magazineRepository;

    @Autowired
    private PlaceCategoryRepository placeCategoryRepository;

    @Autowired
    private PlaceMagazineRepository placeMagazineRepository;

    @Autowired
    private UserSavedPlaceRepository userSavedPlaceRepository;

    @Test
    @DisplayName("유저가 저장한 장소 목록을 성공적으로 조회한다")
    void getUserSavePlace_Success() {
        // Given
        Category category = createCategory(CategoryType.RESTAURANT);
        Magazine magazine = createMagazine("테스트 매거진");
        User user = createUser();
        Place place = createPlaceWithRelations(
                "테스트 장소",
                BigDecimal.valueOf(37.5665),
                BigDecimal.valueOf(126.9780),
                category,
                magazine
        );

        SavedPlace savedPlace = createSavedPlace(place, user);
        createPlaceSavedPlace(place, savedPlace);

        // When
        List<PlaceSearchResponse> result = placeService.getUserSavePlace(user.getEmail());

        // Then
        assertThat(result).hasSize(1);
        PlaceSearchResponse response = result.get(0);
        assertThat(response)
                .satisfies(r -> {
                    assertThat(r.placeId()).isEqualTo(place.getId());
                    assertThat(r.name()).isEqualTo("테스트 장소");
                    assertThat(r.shortDescription()).isEqualTo("테스트 설명");
                    assertThat(r.categoryName()).isEqualTo("음식점");
                    assertThat(r.magazineTitle()).isEqualTo("테스트 매거진");
                    assertThat(r.placeImageUrl()).containsExactly("test-image-url.jpg");
                    assertThat(r.instagramLink()).isEqualTo("instagram.com");
                    assertThat(r.naverLink()).isEqualTo("naver.com");
                });
    }

    @Test
    @DisplayName("유저가 저장한 장소가 없는 경우 빈 리스트를 반환한다")
    void getUserSavePlace_EmptyList() {
        // Given
        User user = createUser();

        // When
        List<PlaceSearchResponse> result = placeService.getUserSavePlace(user.getEmail());

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("특정 플레이스를 성공적으로 조회한다")
    void getPlace_Success() {
        // Given
        Category category = createCategory(CategoryType.RESTAURANT);
        Magazine magazine = createMagazine("테스트 매거진");

        Place place = createPlaceWithRelations(
                "테스트 장소",
                BigDecimal.valueOf(37.5665),
                BigDecimal.valueOf(126.9780),
                category,
                magazine
        );

        // When
        PlaceSearchResponse result = placeService.getPlace(place.getId());

        // Then
        assertThat(result)
                .satisfies(r -> {
                    assertThat(r.placeId()).isEqualTo(place.getId());
                    assertThat(r.name()).isEqualTo("테스트 장소");
                    assertThat(r.shortDescription()).isEqualTo("테스트 설명");
                    assertThat(r.categoryName()).isEqualTo("음식점");
                    assertThat(r.magazineTitle()).isEqualTo("테스트 매거진");
                    assertThat(r.placeImageUrl()).containsExactly("test-image-url.jpg");
                    assertThat(r.instagramLink()).isEqualTo("instagram.com");
                    assertThat(r.naverLink()).isEqualTo("naver.com");
                });
    }

    @Test
    @DisplayName("장소 저장 및 저장 취소를 성공적으로 토글한다")
    void togglePlaceUser_Success() {
        // Given
        User user = createUser();
        Place place = createPlace("테스트 장소",
                BigDecimal.valueOf(37.5665),
                BigDecimal.valueOf(126.9780));

        // When
        boolean firstToggle = placeService.togglePlaceUser(user.getEmail(), place.getId());

        // Then
        assertThat(firstToggle).isTrue();
        assertThat(savedPlaceRepository.findByUserIdAndName(user.getId(), place.getName())).isPresent();

        // When - 두 번째 토글 (저장 취소)
        boolean secondToggle = placeService.togglePlaceUser(user.getEmail(), place.getId());

        // Then
        assertThat(secondToggle).isFalse();
        assertThat(savedPlaceRepository.findByUserIdAndName(user.getId(), place.getName())).isEmpty();
    }

    @Test
    @DisplayName("근처 장소를 성공적으로 검색한다")
    void searchNearbyPlaces_Success() {
        // Given
        Category category = createCategory(CategoryType.RESTAURANT);
        Magazine magazine = createMagazine("테스트 매거진");

        // 가까운 장소와 먼 장소 생성
        Place nearPlace = createPlaceWithRelations(
                "가까운 장소",
                BigDecimal.valueOf(12.3456789),
                BigDecimal.valueOf(98.7654321),
                category,
                magazine
        );

        Place farPlace = createPlaceWithRelations(
                "먼 장소",
                BigDecimal.valueOf(12.3556789),
                BigDecimal.valueOf(98.7754321),
                category,
                magazine
        );

        // When - 가까운 장소의 위경도를 기준으로 500m 반경 검색
        List<Place> result = placeService.searchNearbyPlaces(
                BigDecimal.valueOf(12.3456789),
                BigDecimal.valueOf(98.7654321),
                500.0,  // 500m 반경
                List.of(category.getId()),
                List.of(magazine.getId())
        );

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("가까운 장소");
    }

    // 테스트 데이터 생성 헬퍼 메서드들
    private User createUser() {
        User user = User.builder()
                .email("test@example.com")
                .provider(Provider.EMAIL)
                .nickname("테스터")
                .build();
        return userRepository.save(user);
    }

    private Category createCategory(CategoryType type) {
        Category category = Category.builder()
                .type(type)
                .build();
        return categoryRepository.save(category);
    }

    private Magazine createMagazine(String title) {
        Magazine magazine = Magazine.builder()
                .title(title)
                .build();
        return magazineRepository.save(magazine);
    }

    private Place createPlace(String name, BigDecimal latitude, BigDecimal longitude) {
        Place place = Place.builder()
                .name(name)
                .address("테스트 주소")
                .shortDescription("테스트 설명")
                .latitude(latitude)
                .longitude(longitude)
                .instagramLink("instagram.com")
                .naverplaceLink("naver.com")
                .build();
        return placeRepository.save(place);
    }

    private PlaceCategory createPlaceCategory(Place place, Category category) {
        PlaceCategory placeCategory = PlaceCategory.builder()
                .place(place)
                .category(category)
                .build();
        return placeCategoryRepository.save(placeCategory);
    }

    private PlaceMagazine createPlaceMagazine(Place place, Magazine magazine) {
        PlaceMagazine placeMagazine = PlaceMagazine.builder()
                .place(place)
                .magazine(magazine)
                .build();
        return placeMagazineRepository.save(placeMagazine);
    }

    private SavedPlace createSavedPlace(Place place, User user) {
        SavedPlace savedPlace = SavedPlace.builder()
                .name(place.getName())
                .description(place.getShortDescription())
                .isPublic(true)
                .user(user)
                .build();
        return savedPlaceRepository.save(savedPlace);
    }

    private UserSavedPlace createPlaceSavedPlace(Place place, SavedPlace savedPlace) {
        UserSavedPlace userSavedPlace = UserSavedPlace.builder()
                .place(place)
                .savedPlace(savedPlace)
                .build();
        return userSavedPlaceRepository.save(userSavedPlace);
    }

    private PlaceImage createPlaceImage(Place place, String url) {
        PlaceImage placeImage = PlaceImage.builder()
                .place(place)
                .url(url)
                .sequence(1)
                .build();
        place.getPlaceImages().add(placeImage);
        return placeRepository.save(place).getPlaceImages().get(0);
    }

    // 복합 생성 헬퍼 메서드
    private Place createPlaceWithRelations(String name, BigDecimal latitude, BigDecimal longitude,
                                           Category category, Magazine magazine) {
        Place place = createPlace(name, latitude, longitude);

        createPlaceCategory(place, category);
        createPlaceMagazine(place, magazine);
        createPlaceImage(place, "test-image-url.jpg");

        return place;
    }
}

