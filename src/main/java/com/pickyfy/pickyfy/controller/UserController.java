package com.pickyfy.pickyfy.controller;

import com.pickyfy.pickyfy.apiPayload.ApiResponse;
import com.pickyfy.pickyfy.dto.request.UserCreateRequest;
import com.pickyfy.pickyfy.dto.response.UserCreateResponse;
import com.pickyfy.pickyfy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController implements UserControllerApi{

    private final UserService userService;

    @PostMapping("/signup")
    @Override
    public ApiResponse<UserCreateResponse> signUp(UserCreateRequest userCreateRequest) {
        UserCreateResponse userCreateResponse = userService.signUp(userCreateRequest);
        return ApiResponse.onSuccess(userCreateResponse);
    }
}
