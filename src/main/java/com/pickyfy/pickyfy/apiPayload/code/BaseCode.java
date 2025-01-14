package com.pickyfy.pickyfy.apiPayload.code;

import com.pickyfy.pickyfy.apiPayload.code.dto.ResponseDTO;

public interface BaseCode {
    ResponseDTO getReason();
    ResponseDTO getReasonHttpStatus();
}
