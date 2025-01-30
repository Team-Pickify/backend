package com.pickyfy.pickyfy.exception;

import com.pickyfy.pickyfy.apiPayload.code.BaseErrorCode;
import com.pickyfy.pickyfy.apiPayload.code.dto.ErrorResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

    private BaseErrorCode code;

    public ErrorResponseDTO getErrorReasonHttpStatus(){
        return this.code.getReasonHttpStatus();
    }
}
