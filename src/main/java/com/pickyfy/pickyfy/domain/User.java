package com.pickyfy.pickyfy.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(nullable = false)
    private Provider provider;

    private Long providerId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserSavedPlace> savedPlaces = new ArrayList<>();

    @Builder
    public User(String email, String password, String nickname, String profileImage, Provider provider, Long providerId) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.provider = provider;
        this.providerId = providerId;
        this.profileImage = profileImage;
    }

    public void updateUserNickname(String nickname){
        if (nickname != null){
            this.nickname = nickname;
        }
    }

    public void updateProfileImage(String profileImage){
        if (profileImage != null){
            this.profileImage = profileImage;
        }
    }

}