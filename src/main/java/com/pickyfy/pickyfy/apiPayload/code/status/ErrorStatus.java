package com.pickyfy.pickyfy.apiPayload.code.status;

import com.pickyfy.pickyfy.apiPayload.code.BaseErrorCode;
import com.pickyfy.pickyfy.apiPayload.code.dto.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 서버 에러
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _GATEWAY_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "COMMON504", "서버 에러, 관리자에게 문의 바랍니다."),

    // 일반 에러
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),
    _NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404", "찾을 수 없는 요청입니다."),

    // 유저 관련 에러
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER4001", "존재하지 않는 유저입니다."),
    EMAIL_NOT_EXIST(HttpStatus.BAD_REQUEST, "USER4002", "이메일은 필수입니다."),
    EMAIL_INVALID(HttpStatus.BAD_REQUEST, "USER4003", "잘못된 이메일 형식입니다."),
    EMAIL_DUPLICATED(HttpStatus.BAD_REQUEST, "USER4004", "중복된 이메일입니다."),
    PASSWORD_NOT_EXIST(HttpStatus.BAD_REQUEST, "USER4005", "비밀번호는 필수입니다."),
    PASSWORD_INVALID(HttpStatus.BAD_REQUEST, "USER4006", "잘못된 비밀번호 형식입니다."),
    PASSWORD_WRONG(HttpStatus.BAD_REQUEST, "USER4007", "잘못된 비밀번호입니다."),
    NICKNAME_INVALID(HttpStatus.BAD_REQUEST, "USER4008", "잘못된 닉네임 형식입니다."),
    NICKNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "USER4009", "닉네임은 필수입니다."),

    // 플레이스 관련 에러
    PLACE_NOT_FOUND(HttpStatus.BAD_REQUEST, "PLACE4001", "존재하지 않는 플레이스입니다."),
    ADD_PLACE_FAIL(HttpStatus.BAD_REQUEST, "PLACE4002", "[관리자] 플레이스 등록에 실패했습니다."),
    EDIT_PLACE_FAIL(HttpStatus.BAD_REQUEST, "PLACE4003", "[관리자] 플레이스 수정에 실패했습니다."),

    // 매거진 관련 에러
    MAGAZINE_NOT_FOUND(HttpStatus.BAD_REQUEST, "MAGAZINE4001", "존재하지 않는 매거진입니다."),
    ADD_MAGAZINE_FAIL(HttpStatus.BAD_REQUEST, "MAGAZINE4002", "[관리자] 매거진 등록에 실패했습니다."),
    EDIT_MAGAZINE_FAIL(HttpStatus.BAD_REQUEST, "MAGAZINE4003", "[관리자] 매거진 수정에 실패했습니다."),

    // 카테고리 관련 에러
    ADD_CATEGORY_FAIL(HttpStatus.BAD_REQUEST, "CATEGORY4001", "[관리자] 카테고리 등록에 실패했습니다."),
    EDIT_CATEGORY_FAIL(HttpStatus.BAD_REQUEST, "CATEGORY4002", "[관리자] 카테고리 수정에 실패했습니다."),

    // 이미지 관련 에러
    IMAGE_INVALID(HttpStatus.BAD_REQUEST, "IMAGE4001", "잘못된 이미지 형식입니다.");;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus(){
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
