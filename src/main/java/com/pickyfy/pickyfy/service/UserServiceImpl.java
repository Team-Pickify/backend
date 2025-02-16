package com.pickyfy.pickyfy.service;

import com.pickyfy.pickyfy.web.apiResponse.error.ErrorStatus;
import com.pickyfy.pickyfy.common.Constant;
import com.pickyfy.pickyfy.common.util.JwtUtil;
import com.pickyfy.pickyfy.common.util.RedisUtil;
import com.pickyfy.pickyfy.domain.Provider;
import com.pickyfy.pickyfy.domain.User;
import com.pickyfy.pickyfy.web.dto.request.PasswordResetRequest;
import com.pickyfy.pickyfy.web.dto.request.UserCreateRequest;
import com.pickyfy.pickyfy.web.dto.request.UserUpdateRequest;
import com.pickyfy.pickyfy.web.dto.response.UserCreateResponse;
import com.pickyfy.pickyfy.exception.ExceptionHandler;
import com.pickyfy.pickyfy.repository.UserRepository;
import com.pickyfy.pickyfy.web.dto.response.UserInfoResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final S3Service s3Service;

    @Transactional
    public UserCreateResponse signUp(UserCreateRequest request) {
        validateEmailToken(request.email(), request.emailToken());
        validateEmailDuplicated(request.email());

        User user = toUserEntity(request);
        userRepository.save(user);
        return new UserCreateResponse(request.nickname());
    }

    public UserInfoResponse getUser(String email){
        User user = findUserByEmail(email);
        return UserInfoResponse.from(user);
    }

    @Transactional
    public Long updateUser(String email, UserUpdateRequest request){
        User user = findUserByEmail(email);
        String nickname = request.nickname();
        MultipartFile profileImage = request.profileImage();

        if (nickname != null){
            user.updateUserNickname(nickname);
        }
        if (profileImage != null){
            user.updateProfileImage(s3Service.upload(profileImage));
        }
        return user.getId();
    }

    @Transactional
    public void signOut(String email){
        User user = findUserByEmail(email);
        userRepository.delete(user);
        invalidateTokens(user.getEmail());
    }

    @Transactional
    public void resetPassword(PasswordResetRequest request){
        User user = findUserByEmail(request.email());

        validateEmailToken(request.email(), request.emailToken());

        User updatedUser = user.toBuilder()
                .password(passwordEncoder.encode(request.newPassword()))
                .build();

        userRepository.save(updatedUser);
    }

    public void verifyByEmail(String email){
        findUserByEmail(email);
    }

    private void invalidateTokens(String userEmail){
        redisUtil.deleteRefreshToken(Constant.REDIS_KEY_PREFIX + userEmail);
    }

    private void validateEmailToken(String email, String token){
        if(!email.equals(jwtUtil.getPrincipal(token))){
            throw new ExceptionHandler(ErrorStatus.EMAIL_INVALID);
        }
    }

    private User toUserEntity(UserCreateRequest request){
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

    private void validateEmailDuplicated(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ExceptionHandler(ErrorStatus.EMAIL_DUPLICATED);
        }
    }
}