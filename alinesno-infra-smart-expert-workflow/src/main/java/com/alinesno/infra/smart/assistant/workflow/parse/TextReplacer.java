package com.alinesno.infra.smart.assistant.workflow.parse;

import java.util.Map;

/**
 * 该类用于处理文本中占位符的替换操作。
 * 它可以将文本里特定格式（{{xxx.xxx}}）的占位符，根据传入的 output 映射表，替换为对应的实际值。
 */
public class TextReplacer {

    /**
     * 此方法用于将传入文本中的占位符替换为 output 映射表中对应的值。
     * 占位符的格式为 {{xxx.xxx}}，其中 xxx.xxx 是 output 映射表中的键。
     * 如果 output 中存在该键，则将占位符替换为对应的值；若不存在，则保留占位符不变。
     *
     * @param text  需要进行占位符替换的原始文本。
     * @param output 一个映射表，键为占位符中的标识（如 xxx.xxx），值为要替换的实际内容。
     * @return 经过占位符替换后的最终文本。
     */
    public static String replacePlaceholders(String text, Map<String, Object> output) {
        // 遍历 output 映射表中的每个键值对
        for (Map.Entry<String, Object> entry : output.entrySet()) {
            // 构建完整的占位符，格式为 {{key}}
            String placeholder = "{{" + entry.getKey() + "}}";
            // 获取要替换的值
            Object value = entry.getValue();
            // 将文本中所有匹配的占位符替换为对应的值
            text = text.replace(placeholder, value.toString());
        }
        return text;
    }

}