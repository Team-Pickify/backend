package com.pickyfy.pickyfy.exception;

import com.pickyfy.pickyfy.web.apiResponse.error.ErrorStatus;

public class ExceptionHandler extends GeneralException {
    public ExceptionHandler(ErrorStatus errorStatus){
        super(errorStatus);
    }
}