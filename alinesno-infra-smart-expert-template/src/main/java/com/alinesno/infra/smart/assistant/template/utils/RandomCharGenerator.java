package com.alinesno.infra.smart.assistant.template.utils;

import java.util.Random;

public class RandomCharGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int LENGTH = CHARACTERS.length();
    private static final Random RANDOM = new Random();

    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            // Generate a random index within the range of the CHARACTERS string length
            int randomIndex = RANDOM.nextInt(LENGTH);
            // Append the character at the generated index to the StringBuilder
            sb.append(CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
    }
}