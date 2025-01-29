package com.pickyfy.pickyfy.service;

import com.pickyfy.pickyfy.web.dto.request.UserCreateRequest;
import com.pickyfy.pickyfy.web.dto.request.UserUpdateRequest;
import com.pickyfy.pickyfy.web.dto.response.UserCreateResponse;
import com.pickyfy.pickyfy.web.dto.response.UserInfoResponse;
import com.pickyfy.pickyfy.web.dto.response.UserUpdateResponse;

public interface UserService {
    UserCreateResponse signUp(UserCreateRequest request);
    UserUpdateResponse updateUser(String accessToken, UserUpdateRequest request);
    UserInfoResponse getUser(String accessToken);
    void logout(String accessToken);
    void signOut(String accessToken);
}

