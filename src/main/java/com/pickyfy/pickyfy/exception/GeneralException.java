package com.pickyfy.pickyfy.exception;

import com.pickyfy.pickyfy.web.apiResponse.error.ErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {
    private final ErrorStatus errorStatus;
}
