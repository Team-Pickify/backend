package com.pickyfy.pickyfy.apiPayload.code;

import com.pickyfy.pickyfy.apiPayload.code.dto.ReasonDTO;

//
public interface BaseCode {
    ReasonDTO getReason();
    ReasonDTO getReasonHttpStatus();
}
