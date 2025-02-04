package com.pickyfy.pickyfy.exception;

import com.pickyfy.pickyfy.web.apiResponse.common.BaseErrorCode;
import com.pickyfy.pickyfy.web.apiResponse.error.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

    private BaseErrorCode code;

    public ErrorResponse getErrorReasonHttpStatus(){
        return this.code.getReasonHttpStatus();
    }
}
