package com.pickyfy.pickyfy.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "short_description", nullable = false)
    private String shortDescription;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(name = "instagram_link")
    private String instagramLink;

    @Column(name = "naverplace_link")
    private String naverplaceLink;

    @Column(nullable = false, precision = 11, scale = 8)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 11, scale = 8)
    private BigDecimal longitude;

    // 연관된 Place image 삭제 하기 위해 추가
    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlaceImage> placeImages = new ArrayList<>();

    @OneToMany(mappedBy = "place")
    private List<PlaceSavedPlace> placeSavedPlaces = new ArrayList<>();

    @OneToMany(mappedBy = "place")
    private List<PlaceCategory> placeCategories = new ArrayList<>();

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlaceMagazine> placeMagazines = new ArrayList<>();

    @Builder
    public Place(String shortDescription,String name, String address, String instagramLink,
                 String naverplaceLink, BigDecimal latitude, BigDecimal longitude) {
        this.shortDescription = shortDescription;
        this.name = name;
        this.address = address;
        this.instagramLink = instagramLink;
        this.naverplaceLink = naverplaceLink;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void updatePlace(String name, String address, String shortDescription,
                            String instagramLink, String naverplaceLink, BigDecimal latitude, BigDecimal longitude) {
        if (name != null) this.name = name;
        if (address != null) this.address = address;
        if (shortDescription != null) this.shortDescription = shortDescription;
        if (instagramLink != null) this.instagramLink = instagramLink;
        if (naverplaceLink != null) this.naverplaceLink = naverplaceLink;
        if (latitude != null) this.latitude = latitude;
        if (longitude != null) this.longitude = longitude;
    }

    public void updateImages(List<String> newImageUrls) {
        this.placeImages.clear();
        for (String imageUrl : newImageUrls) {
            this.placeImages.add(PlaceImage.builder().place(this).url(imageUrl).build());
        }
    }
}
