package com.pickyfy.pickyfy.web.dto;

import com.pickyfy.pickyfy.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomUserInfoDto {

    private String password;
    private String username;
    private String email;
    private String profileImage;

    public static CustomUserInfoDto from(User user){

        return CustomUserInfoDto.builder()
                .username(user.getNickname())
                .password(user.getPassword())
                .email(user.getEmail())
                .profileImage(user.getProfileImage())
                .build();
    }
}