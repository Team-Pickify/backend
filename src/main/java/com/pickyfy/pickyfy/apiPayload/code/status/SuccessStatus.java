package com.pickyfy.pickyfy.apiPayload.code.status;

import com.pickyfy.pickyfy.apiPayload.code.BaseCode;
import com.pickyfy.pickyfy.apiPayload.code.dto.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {

    // 일반 응답
    _OK(HttpStatus.OK, "COMMON200", "200 성공"),
    _CREATED(HttpStatus.CREATED, "COMMON201", "성공적으로 생성되었습니다."),

    // 유저 관련 응답
    SIGN_IN_SUCCESS(HttpStatus.OK, "USER2011", "성공적으로 회원가입되었습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK, "USER2001", "성공적으로 로그아웃되었습니다."),
    USER_EDIT_SUCCESS(HttpStatus.OK, "USER2002", "유저 정보가 성공적으로 변경되었습니다."),
    USER_SING_OUT_SUCCESS(HttpStatus.OK, "USER2003", "성공적으로 탈퇴되었습니다."),

    // 관리자 페이지 관련 응답
    ADMIN_LOGIN_SUCCESS(HttpStatus.OK, "ADMIN2011", "[관리자] 성공적으로 로그인되었습니다."),
    ADMIN_LOGOUT_SUCCESS(HttpStatus.OK, "ADMIN2012", "[관리자] 성공적으로 로그아웃되었습니다."),
    ADMIN_SIGN_OUT_SUCCESS(HttpStatus.OK, "ADMIN2013", "[관리자] 성공적으로 탈퇴되었습니다."),

    // 플레이스 관련 응답
    SAVE_PLACE_SUCCESS(HttpStatus.OK, "PLACE2001", "플레이스가 성공적으로 저장되었습니다."),
    ADD_PLACE_SUCCESS(HttpStatus.OK, "PLACE2011", "[관리자] 플레이스가 성공적으로 등록되었습니다."),
    EDIT_PLACE_SUCCESS(HttpStatus.OK, "PLACE2002", "[관리자] 플레이스가 성공적으로 수정되었습니다."),
    DELETE_PLACE_SUCCESS(HttpStatus.OK, "PLACE2003", "[관리자] 플레이스가 성공적으로 삭제되었습니다."),

    // 매거진 관련 응답
    ADD_MAGAZINE_SUCCESS(HttpStatus.OK, "MAGAZINE2011", "[관리자] 매거진이 성공적으로 등록되었습니다."),
    EDIT_MAGAZINE_SUCCESS(HttpStatus.OK, "MAGAZINE2001", "[관리자] 매거진이 성공적으로 수정되었습니다."),
    DELETE_MAGAZINE_SUCCESS(HttpStatus.OK, "MAGAZINE2002", "[관리자] 플레이스가 성공적으로 삭제되었습니다."),

    // 카테고리 관련 응답
    ADD_CATEGORY_SUCCESS(HttpStatus.OK, "CATEGORY2010", "[관리자] 카테고리가 성공적으로 등록되었습니다."),
    EDIT_CATEGORY_SUCCESS(HttpStatus.OK, "CATEGORY2001", "[관리자] 카테고리가 성공적으로 수정되었습니다."),
    DELETE_CATEGORY_SUCCESS(HttpStatus.OK, "CATEGORY2002", "[관리자] 카테고리가 성공적으로 삭제되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReason() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .build();
    }

    @Override
    public ReasonDTO getReasonHttpStatus(){
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .httpStatus(httpStatus)
                .build();
    }
}
