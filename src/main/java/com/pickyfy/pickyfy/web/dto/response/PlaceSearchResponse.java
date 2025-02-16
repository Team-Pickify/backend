package com.pickyfy.pickyfy.web.dto.response;

import com.pickyfy.pickyfy.domain.Place;
import com.pickyfy.pickyfy.domain.PlaceImage;
import com.pickyfy.pickyfy.web.dto.MagazineInfo;
import com.pickyfy.pickyfy.web.dto.PlaceSearchResponseParams;
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

    public static PlaceSearchResponse from(PlaceSearchResponseParams params){
        return PlaceSearchResponse.builder()
                .placeId(params.place().getId())
                .name(params.place().getName())
                .shortDescription(params.place().getShortDescription())
                .latitude(params.place().getLatitude())
                .longitude(params.place().getLongitude())
                .createdAt(params.place().getCreatedAt())
                .updatedAt(params.place().getUpdatedAt())
                .placeImageUrl(params.placeImageUrls())
                .categoryName(params.categoryName())
                .magazineTitle(params.magazineInfo().title())
                .instagramLink(params.place().getInstagramLink())
                .naverLink(params.place().getNaverplaceLink())
                .magazineIconUrl(params.magazineInfo().iconUrl())
                .placeImageId(params.place().getPlaceImages().stream().map(PlaceImage::getId).toList())
                .build();
    }
}
