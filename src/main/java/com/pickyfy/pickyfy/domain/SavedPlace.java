package com.pickyfy.pickyfy.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SavedPlace extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "savedPlace", cascade = CascadeType.ALL)
    private List<PlaceSavedPlace> placeSavedPlaces = new ArrayList<>();

    @Builder
    public SavedPlace(String name, String description, boolean isPublic, User user) {
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
        this.user = user;
    }
}
