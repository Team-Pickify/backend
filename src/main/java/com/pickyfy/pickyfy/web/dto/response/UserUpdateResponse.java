package com.pickyfy.pickyfy.web.dto.response;

import com.pickyfy.pickyfy.domain.User;

public record UserUpdateResponse (
        String nickname, String profileImage
){
    public static UserUpdateResponse from(User user){
        return new UserUpdateResponse(
                user.getNickname(),
                user.getProfileImage()
        );
    }
}
