package com.pickyfy.pickyfy.auth.details;

import com.pickyfy.pickyfy.domain.Admin;
import com.pickyfy.pickyfy.domain.User;
import com.pickyfy.pickyfy.repository.AdminRepository;
import com.pickyfy.pickyfy.web.dto.CustomAdminInfo;
import com.pickyfy.pickyfy.web.dto.CustomUserInfo;
import com.pickyfy.pickyfy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

    private final static String NOT_FOUND_USER = "해당하는 유저가 없습니다.";

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String nickname) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByName(nickname)
                .orElseThrow(() -> new UsernameNotFoundException(NOT_FOUND_USER));
        CustomAdminInfo customAdminInfoDto = CustomAdminInfo.from(admin);

        return new CustomAdminDetails(customAdminInfoDto);
    }

    public UserDetails loadUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(NOT_FOUND_USER));
        CustomUserInfo customUserInfoDto = CustomUserInfo.from(user);

        return new CustomUserDetails(customUserInfoDto);
    }
}