package com.pickyfy.pickyfy.exception;

import com.pickyfy.pickyfy.apiPayload.code.BaseErrorCode;

public class DuplicateResourceException extends GeneralException {
    public DuplicateResourceException(BaseErrorCode baseErrorCode) {
        super(baseErrorCode);
    }
}
