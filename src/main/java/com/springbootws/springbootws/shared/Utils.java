package com.springbootws.springbootws.shared;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;

@Component
public class Utils {

    private final Random RANDOM = new SecureRandom();
    private final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public String generateUserId(int length){
        return generateRandomString(length);
    }

    public String generateGddressId(int length){
        return generateRandomString(length);
    }

    private String generateRandomString(int length){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <length ; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
