package com.pickyfy.pickyfy.auth.custom;

import com.pickyfy.pickyfy.dto.AdminInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class AdminDetails implements UserDetails {

    private final AdminInfoDto adminInfoDto;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return adminInfoDto.password();
    }

    @Override
    public String getUsername() {
        return adminInfoDto.name();
    }

}
