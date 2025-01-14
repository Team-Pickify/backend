package com.pickyfy.pickyfy.apiPayload.code.status;

import com.pickyfy.pickyfy.apiPayload.code.BaseCode;
import com.pickyfy.pickyfy.apiPayload.code.dto.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {

    _OK(HttpStatus.OK, StatusCode.COMMON.getCode(200), "요청이 성공적으로 처리되었습니다."),
    _CREATED(HttpStatus.CREATED, StatusCode.COMMON.getCode(201), "요청이 성공적으로 생성되었습니다."),

    SIGN_IN_SUCCESS(HttpStatus.OK, StatusCode.USER.getCode(2011), "성공적으로 회원가입되었습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK, StatusCode.USER.getCode(2001), "성공적으로 로그아웃되었습니다."),
    USER_EDIT_SUCCESS(HttpStatus.OK, StatusCode.USER.getCode(2002), "유저 정보가 성공적으로 변경되었습니다."),
    USER_SING_OUT_SUCCESS(HttpStatus.OK, StatusCode.USER.getCode(2003), "성공적으로 탈퇴되었습니다."),

    ADMIN_LOGIN_SUCCESS(HttpStatus.OK, StatusCode.ADMIN.getCode(2011), "[관리자] 성공적으로 로그인되었습니다."),
    ADMIN_LOGOUT_SUCCESS(HttpStatus.OK, StatusCode.ADMIN.getCode(2012), "[관리자] 성공적으로 로그아웃되었습니다."),
    ADMIN_SIGN_OUT_SUCCESS(HttpStatus.OK, StatusCode.ADMIN.getCode(2013), "[관리자] 성공적으로 탈퇴되었습니다."),

    SAVE_PLACE_SUCCESS(HttpStatus.OK, StatusCode.PLACE.getCode(2001), "플레이스가 성공적으로 저장되었습니다."),
    ADD_PLACE_SUCCESS(HttpStatus.OK, StatusCode.PLACE.getCode(2011), "[관리자] 플레이스가 성공적으로 등록되었습니다."),
    EDIT_PLACE_SUCCESS(HttpStatus.OK, StatusCode.PLACE.getCode(2002), "[관리자] 플레이스가 성공적으로 수정되었습니다."),
    DELETE_PLACE_SUCCESS(HttpStatus.OK, StatusCode.PLACE.getCode(2003), "[관리자] 플레이스가 성공적으로 삭제되었습니다."),

    ADD_MAGAZINE_SUCCESS(HttpStatus.OK, StatusCode.MAGAZINE.getCode(2011), "[관리자] 매거진이 성공적으로 등록되었습니다."),
    EDIT_MAGAZINE_SUCCESS(HttpStatus.OK, StatusCode.MAGAZINE.getCode(2001), "[관리자] 매거진이 성공적으로 수정되었습니다."),
    DELETE_MAGAZINE_SUCCESS(HttpStatus.OK, StatusCode.MAGAZINE.getCode(2002), "[관리자] 플레이스가 성공적으로 삭제되었습니다."),

    ADD_CATEGORY_SUCCESS(HttpStatus.OK, StatusCode.CATEGORY.getCode(2011), "[관리자] 카테고리가 성공적으로 등록되었습니다."),
    EDIT_CATEGORY_SUCCESS(HttpStatus.OK, StatusCode.CATEGORY.getCode(2001), "[관리자] 카테고리가 성공적으로 수정되었습니다."),
    DELETE_CATEGORY_SUCCESS(HttpStatus.OK, StatusCode.CATEGORY.getCode(2002), "[관리자] 카테고리가 성공적으로 삭제되었습니다.");

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
