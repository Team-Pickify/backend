package com.pickyfy.pickyfy.service;

import com.pickyfy.pickyfy.apiPayload.code.status.ErrorStatus;
import com.pickyfy.pickyfy.common.util.JwtUtil;
import com.pickyfy.pickyfy.domain.Provider;
import com.pickyfy.pickyfy.domain.User;
import com.pickyfy.pickyfy.web.dto.request.UserCreateRequest;
import com.pickyfy.pickyfy.web.dto.request.UserUpdateRequest;
import com.pickyfy.pickyfy.web.dto.response.UserCreateResponse;
import com.pickyfy.pickyfy.exception.handler.ExceptionHandler;
import com.pickyfy.pickyfy.repository.UserRepository;
import com.pickyfy.pickyfy.web.dto.response.UserUpdateResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    @Override
    public UserCreateResponse signUp(UserCreateRequest userCreateRequest) {
        validateEmailToken(userCreateRequest.email(), userCreateRequest.emailToken());
        User user = toEntity(userCreateRequest);
        userRepository.save(user);
        return new UserCreateResponse(userCreateRequest.nickname());
    }

    private void validateEmailToken(String email, String token){
        if(!email.equals(jwtUtil.getUserEmail(token))){
            throw new ExceptionHandler(ErrorStatus.EMAIL_INVALID);
        }
    }

    private User toEntity(UserCreateRequest userCreateRequest){
        return User.builder()
                .nickname(userCreateRequest.nickname())
                .password(passwordEncoder.encode(userCreateRequest.password()))
                .email(userCreateRequest.email())
                .provider(Provider.EMAIL)
                .build();
    }

    @Transactional
    public UserUpdateResponse update(UserUpdateRequest userUpdateRequest, String userEmail){
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));

        User updatedUser = user.toBuilder()
                .nickname(userUpdateRequest.nickname() != null ? userUpdateRequest.nickname() : user.getNickname())
                .profileImage(userUpdateRequest.profileImage() != null ? userUpdateRequest.profileImage() : user.getProfileImage())
                .build();

        userRepository.save(updatedUser);

        return new UserUpdateResponse(updatedUser.getNickname(), updatedUser.getProfileImage());
    }

    @Transactional
    public void signOut(String userEmail){
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));

        userRepository.delete(user);

        //TODO: 토큰 전부 삭제(jwt, redis)
    }


}
