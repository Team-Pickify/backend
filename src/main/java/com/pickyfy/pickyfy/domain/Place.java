package com.pickyfy.pickyfy.domain;

import com.pickyfy.pickyfy.service.S3Service;
import com.pickyfy.pickyfy.web.dto.request.PlaceCreateRequest;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

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

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL)
    private List<UserSavedPlace> userSavedPlaces = new ArrayList<>();

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL)
    private final List<PlaceImage> placeImages = new ArrayList<>();

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL)
    private final  List<PlaceCategory> placeCategories = new ArrayList<>();

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL)
    private final List<PlaceMagazine> placeMagazines = new ArrayList<>();

    @Builder
    public Place(String shortDescription, String name, String address, String instagramLink,
                 String naverplaceLink, BigDecimal latitude, BigDecimal longitude, List<MultipartFile> images, S3Service s3Service) {
        this.shortDescription = shortDescription;
        this.name = name;
        this.address = address;
        this.instagramLink = instagramLink;
        this.naverplaceLink = naverplaceLink;
        this.latitude = latitude;
        this.longitude = longitude;
        addImages(images, s3Service);
    }

    private void addImages(List<MultipartFile> images, S3Service s3Service){
        if (images == null || images.isEmpty()) {
            return;
        }

        for (int i=0; i<images.size(); i++){
            String imageUrl = s3Service.upload(images.get(i));
            this.placeImages.add(PlaceImage.builder()
                    .place(this)
                    .url(imageUrl)
                    .sequence(i)
                    .build());
        }
    }

    public void updatePlace(PlaceCreateRequest request) {
        Optional.ofNullable(request.name()).ifPresent(value -> this.name = value);
        Optional.ofNullable(request.address()).ifPresent(value -> this.address = value);
        Optional.ofNullable(request.shortDescription()).ifPresent(value -> this.shortDescription = value);
        Optional.ofNullable(request.instagramLink()).ifPresent(value -> this.instagramLink = value);
        Optional.ofNullable(request.naverPlaceLink()).ifPresent(value -> this.naverplaceLink = value);
        Optional.ofNullable(request.latitude()).ifPresent(value -> this.latitude = value);
        Optional.ofNullable(request.longitude()).ifPresent(value -> this.longitude = value);
    }

    public void updateImages(List<MultipartFile> newImages, S3Service s3Service) {
        for (int i = 0; i < newImages.size(); i++) {
            String imageUrl = s3Service.upload(newImages.get(i));
            this.placeImages.add(PlaceImage.builder()
                    .place(this)
                    .url(imageUrl)
                    .sequence(i)
                    .build());
        }
    }
}
