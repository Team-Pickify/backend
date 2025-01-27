package com.pickyfy.pickyfy.auth.custom;

import com.pickyfy.pickyfy.domain.User;
import com.pickyfy.pickyfy.web.dto.CustomUserInfoDto;
import com.pickyfy.pickyfy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

    private final static String NOT_FOUND_USER = "해당하는 유저가 없습니다.";

    private final UserRepository userRepository;

    //메서드
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(NOT_FOUND_USER));
        CustomUserInfoDto customUserInfoDto = CustomUserInfoDto.from(user);

        return new CustomUserDetails(customUserInfoDto);
    }

//    @Override
//    public UserDetails loadUserByUsername(String nickname) throws UsernameNotFoundException {
//        User user = userRepository.findByNickname(nickname)
//                .orElseThrow(() -> new UsernameNotFoundException(NOT_FOUND_USER));
//        CustomUserInfoDto customUserInfoDto = CustomUserInfoDto.from(user);
//
//        return new CustomUserDetails(customUserInfoDto);
//    }

    public UserDetails loadUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(NOT_FOUND_USER));
        CustomUserInfoDto customUserInfoDto = CustomUserInfoDto.from(user);

        return new CustomUserDetails(customUserInfoDto);
    }
}