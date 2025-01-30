package com.pickyfy.pickyfy.apiPayload.code;

import com.pickyfy.pickyfy.apiPayload.code.dto.ErrorResponseDTO;

public interface BaseErrorCode {
    ErrorResponseDTO getReasonHttpStatus();
}
