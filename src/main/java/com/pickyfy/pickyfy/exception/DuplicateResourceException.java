package com.pickyfy.pickyfy.exception;

import com.pickyfy.pickyfy.web.apiResponse.error.ErrorStatus;

public class DuplicateResourceException extends GeneralException {
    public DuplicateResourceException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
