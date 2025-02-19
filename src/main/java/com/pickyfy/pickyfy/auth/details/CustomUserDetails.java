package com.pickyfy.pickyfy.auth.details;

import com.pickyfy.pickyfy.web.dto.CustomUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final CustomUserInfo customUserInfoDto;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(customUserInfoDto.role()));
    }

    @Override
    public String getPassword() {
        return customUserInfoDto.password();
    }

    @Override
    public String getUsername() {
        return customUserInfoDto.email();
    }
}