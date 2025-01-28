package com.pickyfy.pickyfy.web.dto;

import com.pickyfy.pickyfy.domain.Admin;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomAdminInfoDto {

    private String name;
    private String password;
    private String role;

    public static CustomAdminInfoDto from(Admin admin){

        return CustomAdminInfoDto.builder()
                .name(admin.getName())
                .password(admin.getPassword())
                .role("ADMIN")
                .build();
    }
}
