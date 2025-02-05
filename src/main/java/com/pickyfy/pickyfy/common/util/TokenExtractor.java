package com.pickyfy.pickyfy.common.util;

public class TokenExtractor {
    public static String extract(String authorizationHeader) {
        return authorizationHeader.replace("Bearer ", "");
    }
}
