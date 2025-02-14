package com.pickyfy.pickyfy.web.dto.response;

import com.pickyfy.pickyfy.domain.Magazine;
import com.pickyfy.pickyfy.domain.Place;
import com.pickyfy.pickyfy.domain.PlaceImage;
import com.pickyfy.pickyfy.web.dto.MagazineInfo;
import java.math.BigDecimal;
import java.util.List;

public record NearbyPlaceResponse(
        Long id,
        String name,
        String address,
        String shortDescription,
        BigDecimal latitude,
        BigDecimal longitude,
        String instagramLink,
        String naverLink,
        List<String> imageUrls,
        List<String> categories,
        List<MagazineInfo> magazineInfo
) {
    public static NearbyPlaceResponse from(Place place) {
        // 이미지 URL 추출
        List<String> images = place.getPlaceImages().stream()
                .map(PlaceImage::getUrl)
                .toList();

        // 카테고리 이름 추출
        List<String> categoryNames = place.getPlaceCategories().stream()
                .map(pc -> pc.getCategory().getName())
                .toList();

        // 매거진 이름들을 추출하고 불변 리스트로 만듭니다.
        List<MagazineInfo> magazineInfos = place.getPlaceMagazines().stream()
                .map(pm -> {
                    Magazine magazine = pm.getMagazine();
                    return new MagazineInfo(magazine.getTitle(), magazine.getIconUrl());
                })
                .toList();

        // 새로운 PlaceResponse 레코드 인스턴스를 생성하여 반환합니다.
        return new NearbyPlaceResponse(
                place.getId(),
                place.getName(),
                place.getAddress(),
                place.getShortDescription(),
                place.getLatitude(),
                place.getLongitude(),
                place.getInstagramLink(),
                place.getNaverplaceLink(),
                images,
                categoryNames,
                magazineInfos
        );
    }
}
