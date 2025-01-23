package com.pickyfy.pickyfy.auth.custom;

import com.pickyfy.pickyfy.web.dto.CustomUserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final CustomUserInfoDto customUserInfoDto;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return customUserInfoDto.getPassword();
    }

    @Override
    public String getUsername() {
        return customUserInfoDto.getUsername();
    }

    public String getEmail(){
        return customUserInfoDto.getEmail();
    }
}