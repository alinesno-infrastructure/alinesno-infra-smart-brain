package com.alinesno.infra.smart.assistant.role.utils;

import java.util.regex.Pattern;

public class TokenUtils {

    // 中文通常一个汉字算1-2个token（根据具体模型）
    private static final Pattern CHINESE_PATTERN = Pattern.compile("[\\u4e00-\\u9fa5]");
    
    // 英文单词通常按空格和标点分割
    private static final Pattern ENGLISH_WORD_PATTERN = Pattern.compile("[a-zA-Z]+");
    
    // 特殊符号通常单独算token
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[^\\w\\s\\u4e00-\\u9fa5]");

    /**
     * 估算文本的token数量（近似值）
     * @param text 输入文本
     * @return 估算的token数量
     */
    public static int estimateTokenCount(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        int tokenCount = 0;
        
        // 1. 计算中文字符（通常1个汉字=1-2个token）
        int chineseCharCount = countMatches(CHINESE_PATTERN, text);
        tokenCount += (int) (chineseCharCount * 1.5); // 取中间值
        
        // 2. 计算英文单词（通常1个单词=1个token）
        int englishWordCount = countMatches(ENGLISH_WORD_PATTERN, text);
        tokenCount += englishWordCount;
        
        // 3. 计算特殊符号（通常1个符号=1个token）
        int specialCharCount = countMatches(SPECIAL_CHAR_PATTERN, text);
        tokenCount += specialCharCount;
        
        // 4. 剩余空格和换行符等（通常忽略或少量计算）
        // 这里简单计算剩余字符的1/4作为补充
        int remainingChars = text.length() - chineseCharCount - englishWordCount - specialCharCount;
        tokenCount += remainingChars / 4;
        
        return Math.max(1, tokenCount); // 至少返回1
    }

    private static int countMatches(Pattern pattern, String text) {
        return (int) pattern.matcher(text).results().count();
    }

    /**
     * 根据token数量截取文本
     * @param text 原始文本
     * @param maxTokenCount 最大token数
     * @return 截取后的文本
     */
    public static String truncateByTokenCount(String text, int maxTokenCount) {
        if (estimateTokenCount(text) <= maxTokenCount) {
            return text;
        }
        
        // 简单实现：按比例截取字符（实际应用中可能需要更精确的算法）
        double charsPerToken = (double) text.length() / estimateTokenCount(text);
        int maxChars = (int) (maxTokenCount * charsPerToken * 0.8); // 安全系数
        
        return text.substring(0, Math.min(maxChars, text.length()));
    }
}