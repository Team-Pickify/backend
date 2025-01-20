package com.pickyfy.pickyfy.dto;

import com.pickyfy.pickyfy.domain.Admin;
import lombok.Builder;

@Builder
public record AdminInfoDto(String name, String password) {

    public static AdminInfoDto from(Admin admin) {
        return AdminInfoDto.builder()
                .name(admin.getName())
                .password(admin.getPassword())
                .build();
    }
}
