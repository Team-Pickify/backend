package com.pickyfy.pickyfy.web.dto.response;

public record TokenValidationResult(boolean isValid, String message) {
    public static TokenValidationResult success(String message) {
        return new TokenValidationResult(true, message);
    }

    public static TokenValidationResult failure(String message) {
        return new TokenValidationResult(false, message);
    }
}