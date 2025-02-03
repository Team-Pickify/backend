package com.pickyfy.pickyfy.repository;

import com.pickyfy.pickyfy.domain.Place;
import com.pickyfy.pickyfy.web.dto.NearbyPlaceSearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.core.types.dsl.Expressions;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import static com.pickyfy.pickyfy.domain.QPlace.place;
import static com.pickyfy.pickyfy.domain.QPlaceCategory.placeCategory;
import static com.pickyfy.pickyfy.domain.QPlaceMagazine.placeMagazine;

@Repository
@RequiredArgsConstructor
public class PlaceRepositoryImpl implements PlaceRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public List<Place> searchNearbyPlaces(NearbyPlaceSearchCondition condition) {
        return queryFactory
                .selectFrom(place)
                .distinct()
                .join(place.placeCategories, placeCategory).fetchJoin()
                .join(place.placeMagazines, placeMagazine).fetchJoin()
                .where(
                        locationWithinDistance(condition.latitude(),
                                condition.longitude(),
                                condition.distance()),
                        categoriesIn(condition.categoryIds()),
                        magazinesIn(condition.magazineIds())
                )
                .orderBy(
                        calculateDistance(condition.latitude(),
                                condition.longitude()).asc()
                )
                .fetch();
    }

    // 거리 계산을 위한 표현식
    private BooleanExpression locationWithinDistance(BigDecimal lat, BigDecimal lon, Double distance) {
        if (lat == null || lon == null || distance == null) {
            return null;
        }

        return Expressions.booleanTemplate(
                "ST_Distance_Sphere(point(place.longitude, place.latitude), point({0}, {1})) <= {2}",
                lon, lat, distance
        );
    }

    // 거리 계산식 (정렬용)
    private NumberTemplate<Double> calculateDistance(BigDecimal lat, BigDecimal lon) {
        return Expressions.numberTemplate(Double.class,
                "ST_Distance_Sphere(point(place.longitude, place.latitude), point({0}, {1}))",
                lon, lat
        );
    }

    // 카테고리 필터링
    private BooleanExpression categoriesIn(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return null;
        }
        return placeCategory.category.id.in(categoryIds);
    }

    // 매거진 필터링
    private BooleanExpression magazinesIn(List<Long> magazineIds) {
        if (magazineIds == null || magazineIds.isEmpty()) {
            return null;
        }
        return placeMagazine.magazine.id.in(magazineIds);
    }
}
