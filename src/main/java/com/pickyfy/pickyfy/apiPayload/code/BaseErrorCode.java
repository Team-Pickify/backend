package com.pickyfy.pickyfy.apiPayload.code;

import com.pickyfy.pickyfy.apiPayload.code.dto.ErrorReasonDTO;
//
public interface BaseErrorCode {
    ErrorReasonDTO getReason();
    //ErrorReasonDTO getReasonHttpStatus();
}
