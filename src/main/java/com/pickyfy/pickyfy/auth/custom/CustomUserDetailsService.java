package com.pickyfy.pickyfy.auth.custom;

import com.pickyfy.pickyfy.domain.Admin;
import com.pickyfy.pickyfy.domain.User;
import com.pickyfy.pickyfy.dto.AdminInfoDto;
import com.pickyfy.pickyfy.dto.CustomUserInfoDto;
import com.pickyfy.pickyfy.repository.AdminRepository;
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

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    // admin 로드
    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByName(name)
                .orElseThrow(() -> new UsernameNotFoundException(NOT_FOUND_USER));
        AdminInfoDto adminInfoDto = AdminInfoDto.from(admin);

        return new AdminDetails(adminInfoDto);
    }

    // 일반 유저 로드
    public UserDetails loadUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(NOT_FOUND_USER));
        CustomUserInfoDto customUserInfoDto = CustomUserInfoDto.from(user);

        return new CustomUserDetails(customUserInfoDto);
    }
}