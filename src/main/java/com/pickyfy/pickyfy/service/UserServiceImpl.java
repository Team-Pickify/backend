package com.pickyfy.pickyfy.service;

import com.pickyfy.pickyfy.apiPayload.code.status.ErrorStatus;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

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

    @Override
    public UserInfoResponse getUser(String accessToken){
        String userEmail = jwtUtil.getPrincipal(accessToken);
        User user = findUserByEmail(userEmail);
        return UserInfoResponse.from(user);
    }

    @Transactional
    @Override
    public UserUpdateResponse updateUser(String accessToken, UserUpdateRequest request){
        String userEmail = jwtUtil.getPrincipal(accessToken);
        User user = findUserByEmail(userEmail);

        User updatedUser = user.toBuilder()
                .nickname(request.nickname() != null ? request.nickname() : user.getNickname())
                .profileImage(request.profileImage() != null ? request.profileImage() : user.getProfileImage())
                .build();

        userRepository.save(updatedUser);
        return UserUpdateResponse.from(updatedUser);
    }

    @Override
    @Transactional
    public void logout(String accessToken){
        String userEmail = jwtUtil.getPrincipal(accessToken);

        Long expiration = jwtUtil.getExpirationDate(accessToken);
        redisUtil.blacklistAccessToken(accessToken, expiration);

        String redisKey = REDIS_KEY_PREFIX + userEmail;
        redisUtil.deleteRefreshToken(redisKey);
    }

    @Transactional
    public void signOut(String accessToken){
        String userEmail = jwtUtil.getPrincipal(accessToken);
        User user = findUserByEmail(userEmail);

        userRepository.delete(user);

        Long expiration = jwtUtil.getExpirationDate(accessToken);
        redisUtil.blacklistAccessToken(accessToken, expiration);

        String redisKey = REDIS_KEY_PREFIX + userEmail;
        redisUtil.deleteRefreshToken(redisKey);
    }

    private void validateEmailToken(String email, String token){
        if(!email.equals(jwtUtil.getPrincipal(token))){
            throw new ExceptionHandler(ErrorStatus.EMAIL_INVALID);
        }
    }

    private User toEntity(UserCreateRequest request){
        return User.builder()
                .nickname(request.nickname())
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .provider(Provider.EMAIL)
                .build();
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));
    }

}
