package com.pickyfy.pickyfy.domain;

import com.pickyfy.pickyfy.service.S3Service;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public Place(String shortDescription, String name, String address, String instagramLink,
                 String naverplaceLink, BigDecimal latitude, BigDecimal longitude, List<MultipartFile> images, S3Service s3Service) {
        this.shortDescription = shortDescription;
        this.name = name;
        this.address = address;
        this.instagramLink = instagramLink;
        this.naverplaceLink = naverplaceLink;
        this.latitude = latitude;
        this.longitude = longitude;


        if (images != null && !images.isEmpty()) {
            int index = 0;
            for (MultipartFile image : images) {
                String imageUrl = s3Service.upload(image);
                this.placeImages.add(PlaceImage.builder().place(this).url(imageUrl).sequence(index++).build());
            }
        }
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


    public void updateImages(List<MultipartFile> newImages, S3Service s3Service) {

        int currentSize = this.placeImages.size();

        int availableSlots = 5 - currentSize;
        if (availableSlots <= 0) {
            return;
        }

        for (int i = 0; i < Math.min(newImages.size(), availableSlots); i++) {
            String imageUrl = s3Service.upload(newImages.get(i));
            this.placeImages.add(PlaceImage.builder().place(this).url(imageUrl).sequence(currentSize + i).build());
        }
    }



}
