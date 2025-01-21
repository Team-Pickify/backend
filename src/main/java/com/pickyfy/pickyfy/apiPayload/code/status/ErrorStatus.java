package com.pickyfy.pickyfy.apiPayload.code.status;

import com.pickyfy.pickyfy.apiPayload.code.BaseErrorCode;
import com.pickyfy.pickyfy.apiPayload.code.dto.ErrorResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, StatusCode.COMMON.getCode(500), "서버 에러, 관리자에게 문의 바랍니다."),
    _GATEWAY_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, StatusCode.COMMON.getCode(504), "서버 에러, 관리자에게 문의 바랍니다."),

    _BAD_REQUEST(HttpStatus.BAD_REQUEST, StatusCode.COMMON.getCode(400), "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, StatusCode.COMMON.getCode(401), "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, StatusCode.COMMON.getCode(403), "금지된 요청입니다."),
    _NOT_FOUND(HttpStatus.NOT_FOUND, StatusCode.COMMON.getCode(404), "찾을 수 없는 요청입니다."),

    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, StatusCode.USER.getCode(4001), "존재하지 않는 유저입니다."),
    EMAIL_NOT_EXIST(HttpStatus.BAD_REQUEST, StatusCode.USER.getCode(4002), "이메일은 필수입니다."),
    EMAIL_INVALID(HttpStatus.BAD_REQUEST, StatusCode.USER.getCode(4003), "잘못된 이메일 형식입니다."),
    EMAIL_DUPLICATED(HttpStatus.BAD_REQUEST, StatusCode.USER.getCode(4004), "중복된 이메일입니다."),
    PASSWORD_NOT_EXIST(HttpStatus.BAD_REQUEST, StatusCode.USER.getCode(4005), "비밀번호는 필수입니다."),
    PASSWORD_INVALID(HttpStatus.BAD_REQUEST, StatusCode.USER.getCode(4006), "잘못된 비밀번호 형식입니다."),
    PASSWORD_WRONG(HttpStatus.BAD_REQUEST, StatusCode.USER.getCode(4007), "잘못된 비밀번호입니다."),
    NICKNAME_INVALID(HttpStatus.BAD_REQUEST, StatusCode.USER.getCode(4008), "잘못된 닉네임 형식입니다."),
    NICKNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, StatusCode.USER.getCode(4009), "닉네임은 필수입니다."),
    KEY_NOT_FOUNT(HttpStatus.BAD_REQUEST, StatusCode.USER.getCode(4010), "존재하지 않는 키 값입니다."),
    AUTH_CODE_INVALID(HttpStatus.BAD_REQUEST, StatusCode.USER.getCode(4011), "잘못된 인증 코드입니다."),

    PLACE_NOT_FOUND(HttpStatus.BAD_REQUEST, StatusCode.USER.getCode(4001), "존재하지 않는 플레이스입니다."),
    ADD_PLACE_FAIL(HttpStatus.BAD_REQUEST, StatusCode.USER.getCode(4002), "[관리자] 플레이스 등록에 실패했습니다."),
    EDIT_PLACE_FAIL(HttpStatus.BAD_REQUEST, StatusCode.USER.getCode(4003), "[관리자] 플레이스 수정에 실패했습니다."),

    MAGAZINE_NOT_FOUND(HttpStatus.BAD_REQUEST, StatusCode.MAGAZINE.getCode(4001), "존재하지 않는 매거진입니다."),
    ADD_MAGAZINE_FAIL(HttpStatus.BAD_REQUEST, StatusCode.MAGAZINE.getCode(4002), "[관리자] 매거진 등록에 실패했습니다."),
    EDIT_MAGAZINE_FAIL(HttpStatus.BAD_REQUEST, StatusCode.MAGAZINE.getCode(4003), "[관리자] 매거진 수정에 실패했습니다."),

    ADD_CATEGORY_FAIL(HttpStatus.BAD_REQUEST, StatusCode.CATEGORY.getCode(4001), "[관리자] 카테고리 등록에 실패했습니다."),
    EDIT_CATEGORY_FAIL(HttpStatus.BAD_REQUEST, StatusCode.CATEGORY.getCode(4002), "[관리자] 카테고리 수정에 실패했습니다."),

    IMAGE_INVALID(HttpStatus.BAD_REQUEST, StatusCode.IMAGE.getCode(4001), "잘못된 이미지 형식입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorResponseDTO getReason() {
        return ErrorResponseDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorResponseDTO getReasonHttpStatus(){
        return ErrorResponseDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
