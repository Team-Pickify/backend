package com.pickyfy.pickyfy.web.dto.response;

import com.pickyfy.pickyfy.domain.User;

public record UserInfoResponse (
        String nickname, String profileImage
){
    public static UserInfoResponse from(User user){
        return new UserInfoResponse(
                user.getNickname(),
                user.getProfileImage()
        );
    }
}
