package com.pickyfy.pickyfy.exception;

import com.pickyfy.pickyfy.web.apiResponse.common.BaseErrorCode;

public class ExceptionHandler extends GeneralException {
    public ExceptionHandler(BaseErrorCode errorCode){
        super(errorCode);
    }
}