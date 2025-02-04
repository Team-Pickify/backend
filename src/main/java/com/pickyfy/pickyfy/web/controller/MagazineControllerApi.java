package com.pickyfy.pickyfy.web.controller;

import com.pickyfy.pickyfy.web.apiResponse.common.ApiResponse;
import com.pickyfy.pickyfy.web.dto.request.MagazineCreateRequest;
import com.pickyfy.pickyfy.web.dto.request.MagazineUpdateRequest;
import com.pickyfy.pickyfy.web.dto.response.MagazineResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@Tag(name = "매거진", description = "매거진 관련 API")
public interface MagazineControllerApi {

    @Operation(
            summary = "매거진 생성 API",
            description = "관리자용 매거진 생성 API입니다. 제목, 아이콘 URL을 입력받아 새로운 매거진을 생성합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON200",
                    description = "OK, 성공",
                    content = @Content(schema = @Schema(implementation = Long.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "ERROR400",
                    description = "잘못된 요청"
            )
    })
    @PostMapping("/admin/magazines")
    ApiResponse<Long> createMagazine(
            @Parameter(description = "매거진 생성 정보", required = true)
            @Valid @RequestBody MagazineCreateRequest request
    );

    @Operation(
            summary = "매거진 조회 API",
            description = "특정 ID의 매거진을 조회하는 API입니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON200",
                    description = "OK, 성공",
                    content = @Content(schema = @Schema(implementation = MagazineResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "ERROR404",
                    description = "매거진을 찾을 수 없음"
            )
    })
    @GetMapping("/magazines/{id}")
    ApiResponse<MagazineResponse> getMagazine(
            @Parameter(description = "매거진 ID", required = true)
            @PathVariable Long id
    );

    @Operation(
            summary = "전체 매거진 조회 API",
            description = "모든 매거진을 조회하는 API입니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON200",
                    description = "OK, 성공",
                    content = @Content(schema = @Schema(implementation = List.class))
            )
    })
    @GetMapping("/magazines")
    ApiResponse<List<MagazineResponse>> getAllMagazines();

    @Operation(
            summary = "매거진 수정 API",
            description = "관리자용 매거진 수정 API입니다. 제목, 아이콘 URL을 수정할 수 있습니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON200",
                    description = "OK, 성공",
                    content = @Content(schema = @Schema(implementation = Long.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "ERROR400",
                    description = "잘못된 요청"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "ERROR404",
                    description = "매거진을 찾을 수 없음"
            )
    })
    @PutMapping("/admin/magazines/{id}")
    ApiResponse<Long> updateMagazine(
            @Parameter(description = "매거진 ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "매거진 수정 정보", required = true)
            @Valid @RequestBody MagazineUpdateRequest request
    );

    @Operation(
            summary = "매거진 삭제 API",
            description = "관리자용 매거진 삭제 API입니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON200",
                    description = "OK, 성공",
                    content = @Content(schema = @Schema(implementation = Long.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "ERROR404",
                    description = "매거진을 찾을 수 없음"
            )
    })
    @DeleteMapping("/admin/magazines/{id}")
    ApiResponse<Long> deleteMagazine(
            @Parameter(description = "매거진 ID", required = true)
            @PathVariable Long id
    );
}
