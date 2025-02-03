package com.pickyfy.pickyfy.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Magazine extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column
    private String iconUrl;

    @Column(nullable = false)
    private String content;

    @OneToMany(mappedBy = "magazine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlaceMagazine> placeMagazines = new ArrayList<>();

    @Builder
    public Magazine(String title,String iconUrl, String content) {
        this.title = title;
        this.iconUrl = iconUrl;
        this.content = content;
    }

    /**
     * 비즈니스 메소드
     */
    public void update(String title, String iconUrl, String content) {
        this.title = title;
        this.iconUrl = iconUrl;
        this.content = content;
    }

    public void update(String title) {
        this.title = title;
    }
}
