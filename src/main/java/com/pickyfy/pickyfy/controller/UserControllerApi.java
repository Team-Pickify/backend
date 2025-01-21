package com.pickyfy.pickyfy.controller;

import com.pickyfy.pickyfy.apiPayload.ApiResponse;
import com.pickyfy.pickyfy.dto.request.UserCreateRequest;
import com.pickyfy.pickyfy.dto.response.UserCreateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserControllerApi {

    @Operation(summary = "회원가입 API", description = "회원가입 API입니다. 닉네임, 비밀번호, 이메일,이메일 토큰을 입력해주세요 ")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    ApiResponse<UserCreateResponse> signUp(@RequestBody UserCreateRequest userCreateRequest);

}
