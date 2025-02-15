package com.pickyfy.pickyfy.web.dto;

import com.pickyfy.pickyfy.domain.Admin;

public record CustomAdminInfo(String name, String password, String role) {
    public static CustomAdminInfo from(Admin admin){
        return new CustomAdminInfo(
                admin.getName(),
                admin.getPassword(),
                "ADMIN");
    }
}
