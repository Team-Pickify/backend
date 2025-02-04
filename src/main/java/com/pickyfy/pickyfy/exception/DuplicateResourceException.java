package com.pickyfy.pickyfy.exception;

import com.pickyfy.pickyfy.web.apiResponse.common.BaseErrorCode;

public class DuplicateResourceException extends GeneralException {
    public DuplicateResourceException(BaseErrorCode baseErrorCode) {
        super(baseErrorCode);
    }
}
