package com.pickyfy.pickyfy.exception.handler;

import com.pickyfy.pickyfy.web.apiResponse.common.BaseErrorCode;
import com.pickyfy.pickyfy.exception.GeneralException;

public class ExceptionHandler extends GeneralException {
    public ExceptionHandler(BaseErrorCode errorCode){
        super(errorCode);
    }
}