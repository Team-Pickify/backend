package com.pickyfy.pickyfy.exception;

import com.pickyfy.pickyfy.web.apiResponse.common.BaseErrorCode;

public class InvalidRefreshTokenException extends GeneralException {
    public InvalidRefreshTokenException(BaseErrorCode baseErrorCode) {
        super(baseErrorCode);
    }
}
