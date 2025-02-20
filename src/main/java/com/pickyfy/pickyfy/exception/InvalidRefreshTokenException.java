package com.pickyfy.pickyfy.exception;

import com.pickyfy.pickyfy.web.apiResponse.error.ErrorStatus;

public class InvalidRefreshTokenException extends GeneralException {
    public InvalidRefreshTokenException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
