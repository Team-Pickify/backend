package com.pickyfy.pickyfy.common;

public class Constant {

    public static final String PASSWORD_REGX = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+={}\\[\\]:;\"'<>,.?/-]).{8,}$";
    public static final String NICKNAME_REGX = "^[a-zA-Z가-힣0-9!@#$%^&*()_+={}\\[\\]:;\"'<>,.?/-]{1,8}$";

    public static final long EMAIL_TOKEN_EXPIRATION_TIME = 5 * 60 * 1000;
    public static final long ACCESS_TOKEN_EXPIRATION_TIME = 30 * 60 * 1000;
    public static final long REFRESH_TOKEN_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000;

    public static final String REDIS_KEY_PREFIX = "refresh:";
}
