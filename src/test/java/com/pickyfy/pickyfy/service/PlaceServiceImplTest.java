package com.pickyfy.pickyfy.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.pickyfy.pickyfy.domain.Category;
import com.pickyfy.pickyfy.domain.CategoryType;
import com.pickyfy.pickyfy.domain.Magazine;
import com.pickyfy.pickyfy.domain.Place;
import com.pickyfy.pickyfy.domain.PlaceCategory;
import com.pickyfy.pickyfy.domain.PlaceImage;
import com.pickyfy.pickyfy.domain.PlaceMagazine;
import com.pickyfy.pickyfy.domain.PlaceSavedPlace;
import com.pickyfy.pickyfy.domain.Provider;
import com.pickyfy.pickyfy.domain.SavedPlace;
import com.pickyfy.pickyfy.domain.User;
import com.pickyfy.pickyfy.repository.CategoryRepository;
import com.pickyfy.pickyfy.repository.MagazineRepository;
import com.pickyfy.pickyfy.repository.PlaceCategoryRepository;
import com.pickyfy.pickyfy.repository.PlaceMagazineRepository;
import com.pickyfy.pickyfy.repository.PlaceRepository;
import com.pickyfy.pickyfy.repository.PlaceSavedPlaceRepository;
import com.pickyfy.pickyfy.repository.SavedPlaceRepository;
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
    private PlaceSavedPlaceRepository placeSavedPlaceRepository;

    @Test
    @DisplayName("유저가 저장한 장소 목록을 성공적으로 조회한다")
    void getUserSavePlace_Success() {
        // Given
        // 1. 먼저 필요한 기본 데이터들을 생성합니다
        User user = User.builder()
                .email("test@example.com")
                .provider(Provider.EMAIL)
                .nickname("테스터")
                .build();
        userRepository.save(user);

        Category category = Category.builder()
                .type(CategoryType.ALL)
                .build();
        categoryRepository.save(category);

        Magazine magazine = Magazine.builder()
                .title("테스트 매거진")
                .build();
        magazineRepository.save(magazine);

        // 2. Place 생성 및 저장
        Place place = Place.builder()
                .name("테스트 장소")
                .address("테스트 주소")
                .shortDescription("테스트 설명")
                .latitude(BigDecimal.valueOf(37.5665))
                .longitude(BigDecimal.valueOf(126.9780))
                .instagramLink("instagram.com")
                .naverplaceLink("naver.com")
                .build();
        placeRepository.save(place);

        // 3. Place와 Category, Magazine 연결
        PlaceCategory placeCategory = PlaceCategory.builder()
                .place(place)
                .category(category)
                .build();
        placeCategoryRepository.save(placeCategory);

        PlaceMagazine placeMagazine = PlaceMagazine.builder()
                .place(place)
                .magazine(magazine)
                .build();
        placeMagazineRepository.save(placeMagazine);

        // 4. SavedPlace 생성 및 Place와 연결
        SavedPlace savedPlace = SavedPlace.builder()
                .name(place.getName())
                .description(place.getShortDescription())
                .isPublic(true)
                .user(user)
                .build();
        savedPlaceRepository.save(savedPlace);

        PlaceSavedPlace placeSavedPlace = PlaceSavedPlace.builder()
                .place(place)
                .savedPlace(savedPlace)
                .build();
        placeSavedPlaceRepository.save(placeSavedPlace);

        // 5. Place에 이미지 추가
        PlaceImage placeImage = PlaceImage.builder()
                .place(place)
                .url("test-image-url.jpg")
                .sequence(1)
                .build();
        place.getPlaceImages().add(placeImage);
        placeRepository.save(place);

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
                    assertThat(r.categoryName()).isEqualTo("전체");
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
        User user = User.builder()
                .provider(Provider.EMAIL)
                .email("test@example.com")
                .nickname("테스터")
                .build();
        userRepository.save(user);

        // When
        List<PlaceSearchResponse> result = placeService.getUserSavePlace(user.getEmail());

        // Then
        assertThat(result).isEmpty();
    }
}

