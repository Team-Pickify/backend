package com.pickyfy.pickyfy.service;

import com.pickyfy.pickyfy.web.dto.request.PasswordResetRequest;
import com.pickyfy.pickyfy.web.dto.request.UserCreateRequest;
import com.pickyfy.pickyfy.web.dto.request.UserUpdateRequest;
import com.pickyfy.pickyfy.web.dto.response.UserCreateResponse;
import com.pickyfy.pickyfy.web.dto.response.UserInfoResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserCreateResponse signUp(UserCreateRequest request);
    Long updateUser(String accessToken, UserUpdateRequest request, MultipartFile image);
    UserInfoResponse getUser(String accessToken);
    void logout(String accessToken);
    void signOut(String accessToken);
    void verifyByEmail(String email);
    void resetPassword(PasswordResetRequest request);
}

