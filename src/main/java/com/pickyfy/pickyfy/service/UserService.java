package com.pickyfy.pickyfy.service;

import com.pickyfy.pickyfy.dto.request.UserCreateRequest;
import com.pickyfy.pickyfy.dto.response.UserCreateResponse;

public interface UserService {

    UserCreateResponse signUp(UserCreateRequest userCreateRequest);
}
