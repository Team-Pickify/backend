package com.pickyfy.pickyfy.web.apiResponse.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.pickyfy.pickyfy.web.apiResponse.success.SuccessStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class ApiResponse<T> {
    private final Boolean isSuccess;
    private final String code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    public static <T> ApiResponse<T> onSuccess(T result){
        return new ApiResponse<>(true, SuccessStatus._OK.getCode(), SuccessStatus._OK.getMessage(), result);
    }

    public static <T> ApiResponse<T> onSuccess(SuccessStatus status, T result) {
        return new ApiResponse<>(true, status.getCode(), status.getMessage(), result);
    }

    public static <T> ApiResponse<T> onFailure(String code, String message, T data){
        return new ApiResponse<>(false, code, message, data);
    }
}
