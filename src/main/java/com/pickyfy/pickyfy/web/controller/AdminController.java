package com.pickyfy.pickyfy.web.controller;

import com.pickyfy.pickyfy.apiPayload.ApiResponse;
import com.pickyfy.pickyfy.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController implements AdminControllerApi{

    private final AdminService adminService;

    @PatchMapping("/logout")
    public ApiResponse<String> logout(@RequestHeader("Authorization") String header){
        String token = extractToken(header);
        adminService.logout(token);
        return ApiResponse.onSuccess("로그아웃에 성공했습니다.");
    }

    private String extractToken(String authorizationHeader){
        return authorizationHeader.replace("Bearer ", "");
    }
}
