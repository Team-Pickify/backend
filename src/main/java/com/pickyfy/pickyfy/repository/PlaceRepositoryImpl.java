package com.pickyfy.pickyfy.repository;

import com.pickyfy.pickyfy.domain.Place;
import com.pickyfy.pickyfy.domain.QPlace;
import com.pickyfy.pickyfy.domain.QPlaceCategory;
import com.pickyfy.pickyfy.domain.QPlaceMagazine;
import com.pickyfy.pickyfy.web.dto.NearbyPlaceSearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.pickyfy.pickyfy.domain.QCategory.category;
import static com.pickyfy.pickyfy.domain.QMagazine.magazine;
import static com.pickyfy.pickyfy.domain.QPlace.place;

@Repository
@RequiredArgsConstructor
public class PlaceRepositoryImpl implements PlaceRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Place> searchNearbyPlaces(NearbyPlaceSearchCondition condition) {
        QPlace place = QPlace.place;
        QPlaceCategory placeCategory = QPlaceCategory.placeCategory;
        QPlaceMagazine placeMagazine = QPlaceMagazine.placeMagazine;

        JPAQuery<Place> query = queryFactory
                .selectFrom(place)
                .join(place.placeCategories, placeCategory)
                .join(placeCategory.category, category)
                .join(place.placeMagazines, placeMagazine)
                .join(placeMagazine.magazine, magazine)
                .where(
                        categoryIdsIn(condition.categoryIds()),
                        magazineIdsIn(condition.magazineIds())
                )
                .distinct();


        // 거리 조건 추가
        query.where(locationWithinDistance(condition.latitude(),
                condition.longitude(),
                condition.distance()));

        return query.fetch();
    }


    // 거리 계산을 위한 표현식
    private BooleanExpression locationWithinDistance(BigDecimal lat, BigDecimal lon, Double distance) {
        if (lat == null || lon == null || distance == null) {
            return null;
        }

        // 서울 기준 위도 1도 = 111km, 경도 1도 = 88.8km
        double kmPerLatDegree = 111.0;
        double kmPerLonDegree = 88.8;

        // distance는 미터 단위이므로 km로 변환
        double distanceInKm = distance / 1000.0;

        // 위도, 경도의 허용 범위 계산
        double latDiff = distanceInKm / kmPerLatDegree;
        double lonDiff = distanceInKm / kmPerLonDegree;

        return place.latitude.between(
                lat.subtract(new BigDecimal(latDiff)),
                lat.add(new BigDecimal(latDiff))
        ).and(
                place.longitude.between(
                        lon.subtract(new BigDecimal(lonDiff)),
                        lon.add(new BigDecimal(lonDiff))
                )
        );
    }

    private BooleanExpression categoryIdsIn(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return null;
        }
        return category.id.in(categoryIds);
    }

    private BooleanExpression magazineIdsIn(List<Long> magazineIds) {
        if (magazineIds == null || magazineIds.isEmpty()) {
            return null;
        }
        return magazine.id.in(magazineIds);
    }
}
