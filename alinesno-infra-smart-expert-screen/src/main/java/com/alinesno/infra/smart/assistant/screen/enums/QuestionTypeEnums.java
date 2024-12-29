package com.alinesno.infra.smart.assistant.screen.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 题目类型枚举
 */
@Getter
public enum QuestionTypeEnums  {

    SINGLE_CHOICE("single_choice", "单选题"),
    MULTIPLE_CHOICE("multiple_choice", "多选题"),
    TRUE_FALSE("true_false", "判断题"),
    FILL_IN_BLANK("fill_in_blank", "填空题"),
    SHORT_ANSWER("short_answer", "简答题"),
    CODING("coding", "编程题");

    private final String code;
    private final String label;

    QuestionTypeEnums(String code, String label) {
        this.code = code;
        this.label = label;
    }

    /**
     * 根据code获取对应的枚举实例
     *
     * @param code 枚举的code值
     * @return 对应的枚举实例，如果找不到则返回null
     */
    public static QuestionTypeEnums fromCode(String code) {
        for (QuestionTypeEnums questionType : QuestionTypeEnums.values()) {
            if (questionType.getCode().equals(code)) {
                return questionType;
            }
        }
        return null;
    }

    /**
     * 获取所有题目类型的描述信息
     *
     * @return 包含code和label的列表
     */
    public static List<Map<String, String>> getLabels() {
        List<Map<String, String>> labels = new ArrayList<>();
        for (QuestionTypeEnums type : QuestionTypeEnums.values()) {
            Map<String, String> labelMap = new HashMap<>();
            labelMap.put("code", type.getCode());
            labelMap.put("label", type.getLabel());
            labels.add(labelMap);
        }
        return labels;
    }
}