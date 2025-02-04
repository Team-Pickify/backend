package com.pickyfy.pickyfy.web.apiResponse.error;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class ErrorResponse {
    private final boolean isSuccess;
    private final String code;
    private final String message;

    private ErrorResponse(boolean isSuccess, String code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
