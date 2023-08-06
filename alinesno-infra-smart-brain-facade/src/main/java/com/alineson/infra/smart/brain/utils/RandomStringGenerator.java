package com.alineson.infra.smart.brain.utils;

import java.security.SecureRandom;

/**
 * 生成随机字符串的工具类。
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
public class RandomStringGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int LENGTH = 32;

    /**
     * 生成指定长度的随机字符串。
     *
     * @return 生成的随机字符串。
     */
    public static String generateRandomString() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(LENGTH);

        for (int i = 0; i < LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }
}
