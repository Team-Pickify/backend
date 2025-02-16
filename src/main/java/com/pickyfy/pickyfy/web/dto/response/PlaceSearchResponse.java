package com.pickyfy.pickyfy.web.dto.response;

import com.pickyfy.pickyfy.domain.Place;
import com.pickyfy.pickyfy.domain.PlaceImage;
import com.pickyfy.pickyfy.web.dto.MagazineInfo;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PlaceSearchResponse{
    private Long placeId;
    private String name;
    private String shortDescription;
    private String instagramLink;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> placeImageUrl;
    private List<Long> placeImageId;
    private String categoryName;
    private String magazineTitle;
    private String naverLink;
    private String magazineIconUrl;

    public static PlaceSearchResponse from(Place place,
                                           List<String> placeImageUrls,
                                           String categoryName,
                                           MagazineInfo magazineInfo){
        return PlaceSearchResponse.builder()
                .placeId(place.getId())
                .name(place.getName())
                .shortDescription(place.getShortDescription())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .createdAt(place.getCreatedAt())
                .updatedAt(place.getUpdatedAt())
                .placeImageUrl(placeImageUrls)
                .categoryName(categoryName)
                .magazineTitle(magazineInfo.title())
                .instagramLink(place.getInstagramLink())
                .naverLink(place.getNaverplaceLink())
                .magazineIconUrl(magazineInfo.iconUrl())
                .placeImageId(place.getPlaceImages().stream().map(PlaceImage::getId).toList())
                .build();
    }
}
