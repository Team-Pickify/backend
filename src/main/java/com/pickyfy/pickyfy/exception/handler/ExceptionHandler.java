package com.pickyfy.pickyfy.exception.handler;

import com.pickyfy.pickyfy.apiPayload.code.BaseErrorCode;
import com.pickyfy.pickyfy.exception.GeneralException;

public class ExceptionHandler extends GeneralException {
    public ExceptionHandler(BaseErrorCode errorCode){
        super(errorCode);
    }
}
