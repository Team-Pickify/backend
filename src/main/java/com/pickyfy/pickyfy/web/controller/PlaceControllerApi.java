package com.pickyfy.pickyfy.web.controller;

import com.pickyfy.pickyfy.apiPayload.ApiResponse;
import com.pickyfy.pickyfy.web.dto.response.PlaceSearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface PlaceControllerApi {

    @Operation(summary = "유저가 저장한 Place 전체 조회 API", description = "유저가 저장한 Place 들을 리스트로 전체 조회하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @GetMapping("/")
    ApiResponse<List<PlaceSearchResponse>> getUserSavePlace(@RequestParam Long userId);

    @Operation(summary = "특정 Place 를 조회하는 API", description = "특정 Place 정보를 조회하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @GetMapping("/{placeId}")
    ApiResponse<PlaceSearchResponse> getPlace(@PathVariable("placeId") Long placeId);

    @Operation(summary = "유저 Place 저장 API", description = "유저가 Place 를 저장하거나 삭제하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    ApiResponse<String> togglePlaceUser(
            @RequestParam Long userId,
            @RequestParam Long placeId
    ) ;


}
