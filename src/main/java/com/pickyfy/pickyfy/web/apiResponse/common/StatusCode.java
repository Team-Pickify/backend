package com.pickyfy.pickyfy.web.apiResponse.common;

public enum StatusCode {

    COMMON("COMMON"),
    USER("USER"),
    PLACE("PLACE"),
    MAGAZINE("MAGAZINE"),
    ADMIN("ADMIN"),
    CATEGORY("CATEGORY"),
    IMAGE("IMAGE"),
    EMAIL("EMAIL");

    private final String prefix;

    StatusCode(String prefix){
        this.prefix = prefix;
    }

    public String getCode(int codeNumber){
        return this.prefix + codeNumber;
    }
}
