package com.pickyfy.pickyfy.service;

import com.pickyfy.pickyfy.web.dto.request.UserCreateRequest;
import com.pickyfy.pickyfy.web.dto.request.UserUpdateRequest;
import com.pickyfy.pickyfy.web.dto.response.UserCreateResponse;
import com.pickyfy.pickyfy.web.dto.response.UserUpdateResponse;

public interface UserService {

    UserCreateResponse signUp(UserCreateRequest userCreateRequest);
    UserUpdateResponse update(UserUpdateRequest userUpdateRequest, String email);
}
