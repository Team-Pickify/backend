package com.pickyfy.pickyfy.exception;

import com.pickyfy.pickyfy.apiPayload.code.BaseErrorCode;
import com.pickyfy.pickyfy.apiPayload.code.dto.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

    private BaseErrorCode code;

    public ErrorReasonDTO getErrorReason(){
        return this.code.getReason();
    }

    public ErrorReasonDTO getErrorReasonHttpStatus(){
        return this.code.getReasonHttpStatus();
    }
}
