package com.iBanking.iBanking.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class Generics {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz" + "0123456789";
    private static final int ID_LENGTH = 12;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateRequestId() {
        StringBuilder uniqueId = new StringBuilder(ID_LENGTH);
        for (int i = 0; i < ID_LENGTH; i++) {
            int randomIndex = RANDOM.nextInt(CHARACTERS.length());
            uniqueId.append(CHARACTERS.charAt(randomIndex));
        }
        return uniqueId.toString();
    }
}
