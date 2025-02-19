package com.pickyfy.pickyfy.auth.details;

import com.pickyfy.pickyfy.web.dto.CustomAdminInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class CustomAdminDetails implements UserDetails {

    private final CustomAdminInfo customAdminInfoDto;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(customAdminInfoDto.role()));
    }

    @Override
    public String getPassword() {
        return customAdminInfoDto.password();
    }

    @Override
    public String getUsername() {
        return customAdminInfoDto.name();
    }
}
