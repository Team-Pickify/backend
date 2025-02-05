package com.pickyfy.pickyfy.web.apiResponse.success;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SuccessResponse {
    private final boolean isSuccess;
    private final String code;
    private final String message;

    private SuccessResponse(boolean isSuccess, String code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
