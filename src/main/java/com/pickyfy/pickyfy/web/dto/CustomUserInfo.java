package com.pickyfy.pickyfy.web.dto;

import com.pickyfy.pickyfy.domain.User;

public record CustomUserInfo(
        String password,
        String username,
        String email,
        String profileImage,
        String role
) {
    public static CustomUserInfo from(User user){
        return new CustomUserInfo(
                user.getPassword(),
                user.getNickname(),
                user.getEmail(),
                user.getProfileImage(),
                "USER");
    }
}