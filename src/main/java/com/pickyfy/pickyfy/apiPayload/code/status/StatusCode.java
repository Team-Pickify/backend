package com.pickyfy.pickyfy.apiPayload.code.status;
//
public enum StatusCode {

    COMMON("COMMON"),
    USER("USER"),
    PLACE("PLACE"),
    MAGAZINE("MAGAZINE"),
    ADMIN("ADMIN"),
    CATEGORY("CATEGORY"),
    IMAGE("IMAGE");

    private final String prefix;

    StatusCode(String prefix){
        this.prefix = prefix;
    }

    public String getCode(int codeNumber){
        return this.prefix + codeNumber;
    }
}
