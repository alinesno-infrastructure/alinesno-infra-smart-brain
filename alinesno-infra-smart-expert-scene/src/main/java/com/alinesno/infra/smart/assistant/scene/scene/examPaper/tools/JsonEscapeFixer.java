package com.alinesno.infra.smart.assistant.scene.scene.examPaper.tools;

import java.util.regex.Pattern;

/**
 * JSON 转义修复工具类
 * 修复 LaTeX 数学表达式中的转义问题，使其能被 JSON 解析器正确解析
 */
public class JsonEscapeFixer {

    /**
     * 修复 JSON 字符串中的转义问题（适用于 Java 文本块 `"""`）
     * @param rawJson 原始 JSON 字符串（可能包含未转义的 `\text`, `\,`, `\mu` 等）
     * @return 修复后的 JSON 字符串（所有 `\` 被转义为 `\\`）
     */
    public static String fixEscape(String rawJson) {
        // 1. 先处理 LaTeX 数学表达式中的 `\` 转义
        String fixed = rawJson.replace("\\,", "\\\\,")
                             .replace("\\text", "\\\\text")
                             .replace("\\mu", "\\\\mu")
                             .replace("\\Delta", "\\\\Delta")
                             .replace("\\pi", "\\\\pi")
                             .replace("\\times", "\\\\times");

        // 2. 处理其他可能的 `\` 转义（如 `\n`, `\t` 等标准 JSON 转义保持不变）
        // 使用正则表达式匹配非标准转义的 `\` 并替换为 `\\`
        fixed = Pattern.compile("\\\\(?![nrtbf\"\\\\])").matcher(fixed).replaceAll("\\\\\\\\");

        return fixed;
    }

    /**
     * 检查 JSON 字符串是否可能包含未转义的 `\`
     * @param json 待检查的 JSON 字符串
     * @return 如果可能有问题返回 true，否则 false
     */
    public static boolean mightNeedEscapeFix(String json) {
        return json.contains("\\,") || 
               json.contains("\\text") || 
               json.contains("\\mu") || 
               json.contains("\\Delta") || 
               json.contains("\\pi") || 
               json.contains("\\times");
    }
}