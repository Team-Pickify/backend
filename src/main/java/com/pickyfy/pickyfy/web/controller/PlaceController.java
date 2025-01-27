package com.pickyfy.pickyfy.web.controller;

import com.pickyfy.pickyfy.apiPayload.ApiResponse;
//import com.pickyfy.pickyfy.auth.custom.CustomUserDetails;
import com.pickyfy.pickyfy.web.dto.response.PlaceSearchResponse;
import com.pickyfy.pickyfy.service.PlaceServiceImpl;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/places")
public class PlaceController implements PlaceControllerApi{
    private final PlaceServiceImpl placeService;

    /**
     * 특정 유저의 저장된 플레이스 조회
     * @param userId
     * @return
     */
    @GetMapping("/")
    public ApiResponse<List<PlaceSearchResponse>> getUserSavePlace(@RequestParam Long userId){
        List<PlaceSearchResponse> allPlace = placeService.getUserSavePlace(userId);
        return ApiResponse.onSuccess(allPlace);
    }

    /**
     * 특정 place 를 조회
     * @param placeId
     * @return responseDTO
     */
    @GetMapping("/{placeId}")
    public ApiResponse<PlaceSearchResponse> getPlace(@PathVariable("placeId") Long placeId){
        PlaceSearchResponse response = placeService.getPlace(placeId);
        return ApiResponse.onSuccess(response);
    }

    /**
     *
     * @param placeId
     * @param userId
     * @return
     */
    @PatchMapping("/toggle")
    public ApiResponse<String> togglePlaceUser(
            @RequestParam Long userId,
            @RequestParam Long placeId
    ) {
        boolean isSaved = placeService.togglePlaceUser( userId, placeId);
        if (isSaved) {
            return ApiResponse.onSuccess("Place saved successfully.");
        } else {
            return ApiResponse.onSuccess("Place removed successfully.");
        }
    }
}
