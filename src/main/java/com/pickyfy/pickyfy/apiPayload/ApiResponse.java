package com.pickyfy.pickyfy.apiPayload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.pickyfy.pickyfy.apiPayload.code.BaseCode;
import com.pickyfy.pickyfy.apiPayload.code.status.SuccessStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
//
@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class ApiResponse<T> {

    //private final Boolean isSuccess;
    private final String code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    public static <T> ApiResponse<T> onSuccess(T result){
        return new ApiResponse<>(SuccessStatus._OK.getCode(), SuccessStatus._OK.getMessage(), result);
    }

//    public static <T> ApiResponse<T> of(BaseCode code, T result){
//        return new ApiResponse<>(code.getReasonHttpStatus().getCode(), code.getReasonHttpStatus().getMessage(), result);
//    }

    public static <T> ApiResponse<T> onFailure(String code, String message, T data){
        return new ApiResponse<>(code, message, data);
    }
}
