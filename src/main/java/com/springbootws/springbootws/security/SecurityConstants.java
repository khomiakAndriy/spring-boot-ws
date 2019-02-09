package com.springbootws.springbootws.security;

import com.springbootws.springbootws.SpringApplicationContext;

public class SecurityConstants {

    public static final long EXPIRATION_TIME = 10*24*60*60*1000L;
    public static final long PASSWORD_RESET_EXPIRATION_TIME = 24*60*60*1000L;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING= "Autorization";
    public static final String SIGN_UP_URL= "/users";
    public static final String VERIFICATION_EMAIL_URL= "/users/email-verification";
    public static final String PASSWORD_RESET_REQUEST_URL = "/users/password-reset-request";
    public static final String PASSWORD_RESET_URL = "/users/password-reset";

    public static String getTokenSecret(){

        AppProperties appProperties = SpringApplicationContext.getBean("appProperties", AppProperties.class);
        return appProperties.getTokenSecret();
    }

}
