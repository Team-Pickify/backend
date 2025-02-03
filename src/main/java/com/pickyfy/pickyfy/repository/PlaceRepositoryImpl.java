package com.pickyfy.pickyfy.repository;

import com.pickyfy.pickyfy.domain.Place;
import com.pickyfy.pickyfy.web.dto.NearbyPlaceSearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.core.types.dsl.Expressions;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.pickyfy.pickyfy.domain.QCategory.category;
import static com.pickyfy.pickyfy.domain.QMagazine.magazine;
import static com.pickyfy.pickyfy.domain.QPlace.place;
import static com.pickyfy.pickyfy.domain.QPlaceCategory.placeCategory;
import static com.pickyfy.pickyfy.domain.QPlaceMagazine.placeMagazine;

@Repository
@RequiredArgsConstructor
public class PlaceRepositoryImpl implements PlaceRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private static final double EARTH_RADIUS_METERS = 6371008.7714; // WGS-84 평균 반경

    @Override
    public List<Place> searchNearbyPlaces(NearbyPlaceSearchCondition condition) {
        JPAQuery<Place> query = queryFactory
                .selectFrom(place)
                .distinct();

        // 카테고리 필터링 - LEFT JOIN으로 변경하고 조건을 서브쿼리로 처리
        if (condition.categoryIds() != null && !condition.categoryIds().isEmpty()) {
            query.leftJoin(place.placeCategories, placeCategory)
                    .leftJoin(placeCategory.category, category)
                    .where(place.id.in(
                            JPAExpressions
                                    .select(placeCategory.place.id)
                                    .from(placeCategory)
                                    .where(placeCategory.category.id.in(condition.categoryIds()))
                    ));
        }

        // 매거진 필터링 - LEFT JOIN으로 변경하고 조건을 서브쿼리로 처리
        if (condition.magazineIds() != null && !condition.magazineIds().isEmpty()) {
            query.leftJoin(place.placeMagazines, placeMagazine)
                    .leftJoin(placeMagazine.magazine, magazine)
                    .where(place.id.in(
                            JPAExpressions
                                    .select(placeMagazine.place.id)
                                    .from(placeMagazine)
                                    .where(placeMagazine.magazine.id.in(condition.magazineIds()))
                    ));
        }

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
