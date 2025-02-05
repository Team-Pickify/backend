package com.pickyfy.pickyfy.web.apiResponse.common;

import com.pickyfy.pickyfy.web.apiResponse.error.ErrorResponse;

public interface BaseErrorCode {
    ErrorResponse getReason();
}
