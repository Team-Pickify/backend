package com.pickyfy.pickyfy.service;

import com.pickyfy.pickyfy.web.dto.request.UserCreateRequest;
import com.pickyfy.pickyfy.web.dto.response.UserCreateResponse;

public interface UserService {

    UserCreateResponse signUp(UserCreateRequest userCreateRequest);
}
