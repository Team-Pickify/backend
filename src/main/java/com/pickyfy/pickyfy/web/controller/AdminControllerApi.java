package com.pickyfy.pickyfy.web.controller;

import com.pickyfy.pickyfy.web.apiResponse.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "관리자")
public interface AdminControllerApi {

    @Operation(summary = "관리자 로그아웃 API", description = "관리자 로그아웃 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @PatchMapping("/logout")
    ApiResponse<String> logout(@RequestHeader("Authorization") String header);
}
