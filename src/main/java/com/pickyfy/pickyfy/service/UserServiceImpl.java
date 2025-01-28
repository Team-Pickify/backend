package com.pickyfy.pickyfy.service;

import com.pickyfy.pickyfy.apiPayload.code.status.ErrorStatus;
import com.pickyfy.pickyfy.auth.custom.CustomUserDetails;
import com.pickyfy.pickyfy.common.util.JwtUtil;
import com.pickyfy.pickyfy.common.util.RedisUtil;
import com.pickyfy.pickyfy.domain.Provider;
import com.pickyfy.pickyfy.domain.User;
import com.pickyfy.pickyfy.web.dto.request.UserCreateRequest;
import com.pickyfy.pickyfy.web.dto.request.UserUpdateRequest;
import com.pickyfy.pickyfy.web.dto.response.UserCreateResponse;
import com.pickyfy.pickyfy.exception.handler.ExceptionHandler;
import com.pickyfy.pickyfy.repository.UserRepository;
import com.pickyfy.pickyfy.web.dto.response.UserInfoResponse;
import com.pickyfy.pickyfy.web.dto.response.UserUpdateResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    private static final String REDIS_KEY_PREFIX = "email:";

    @Transactional
    @Override
    public UserCreateResponse signUp(UserCreateRequest request) {
        validateEmailToken(request.email(), request.emailToken());
        User user = toEntity(request);
        userRepository.save(user);
        return new UserCreateResponse(request.nickname());
    }

    private void validateEmailToken(String email, String token){
        if(!email.equals(jwtUtil.getUserEmail(token))){
            throw new ExceptionHandler(ErrorStatus.EMAIL_INVALID);
        }
    }

    //TODO: dto로 빼기
    private User toEntity(UserCreateRequest request){
        return User.builder()
                .nickname(request.nickname())
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .provider(Provider.EMAIL)
                .build();
    }

    @Override
    public UserInfoResponse getUser(){
        CustomUserDetails userDetails = getUserDetails();
        User user = findUserByEmail(userDetails.getEmail());
        return UserInfoResponse.from(user);
    }

    @Transactional
    public UserUpdateResponse updateUser(UserUpdateRequest request){
        CustomUserDetails userDetails = getUserDetails();
        User user = findUserByEmail(userDetails.getEmail());

        User updatedUser = user.toBuilder()
                .nickname(request.nickname() != null ? request.nickname() : user.getNickname())
                .profileImage(request.profileImage() != null ? request.profileImage() : user.getProfileImage())
                .build();

        userRepository.save(updatedUser);
        return UserUpdateResponse.from(updatedUser);
    }

    @Transactional
    public void logout(String accessToken){
        CustomUserDetails userDetails = getUserDetails();
        String redisKey = REDIS_KEY_PREFIX + userDetails.getEmail();
        redisUtil.deleteRefreshToken(redisKey);

        Long expiration = jwtUtil.getExpirationDate(accessToken);
        redisUtil.blacklistAccessToken(accessToken, expiration);
    }

    @Transactional
    public void signOut(String accessToken){
        // 유저 정보 삭제
        CustomUserDetails userDetails = getUserDetails();
        User user = findUserByEmail(userDetails.getEmail());
        userRepository.delete(user);

        // 엑세스 토큰 삭제
        Long expiration = jwtUtil.getExpirationDate(accessToken);
        redisUtil.blacklistAccessToken(accessToken, expiration);

        // 리프레시 토큰 삭제
        String redisKey = REDIS_KEY_PREFIX + userDetails.getEmail();
        redisUtil.deleteRefreshToken(redisKey);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));

    }

    private CustomUserDetails getUserDetails(){
        return (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
