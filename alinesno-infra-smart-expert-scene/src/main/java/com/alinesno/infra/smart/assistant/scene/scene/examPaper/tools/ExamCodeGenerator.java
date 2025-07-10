package com.alinesno.infra.smart.assistant.scene.scene.examPaper.tools;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 试卷码生成工具类
 */
public class ExamCodeGenerator {

    /**
     * 生成6位数字试卷码（纯数字版本）
     * @return 6位数字的试卷码，范围：100000-999999
     */
    public static String generateExamCode() {
        return String.format("%06d", ThreadLocalRandom.current().nextInt(100000, 1000000));
    }

    /**
     * 生成带前缀的试卷码（推荐）
     * @param prefix 前缀字母（如"EX"）
     * @return 前缀+6位数字，如：EX123456
     */
    public static String generateExamCodeWithPrefix(String prefix) {
        return prefix + generateExamCode();
    }

    /**
     * 生成带校验位的试卷码（7位）
     * @return 6位数字+1位校验码，如：1234567
     */
    public static String generateExamCodeWithCheckDigit() {
        int randomNum = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return randomNum + "" + calculateCheckDigit(randomNum);
    }

    // 计算校验位（各位数字之和的个位数）
    private static int calculateCheckDigit(int number) {
        int sum = 0;
        while (number > 0) {
            sum += number % 10;
            number /= 10;
        }
        return sum % 10;
    }
}