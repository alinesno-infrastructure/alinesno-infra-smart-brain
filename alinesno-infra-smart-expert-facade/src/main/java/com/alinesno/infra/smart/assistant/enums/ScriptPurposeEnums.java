package com.alinesno.infra.smart.assistant.enums;

import lombok.Getter;

/**
 * 脚本用途枚举类，用于定义脚本用途。
 */
@Getter
public enum ScriptPurposeEnums {

    EXECUTE("execute", "执行"),
    AUDIT("audit", "审核"),
    FUNCTION_CALL("functionCall", "函数调用");

    private final String value;
    private final String label;

    ScriptPurposeEnums(String value, String label) {
        this.value = value;
        this.label = label;
    }

    // 如果你需要通过value获取对应的ScriptPurpose枚举值，可以添加如下方法
    public static ScriptPurposeEnums fromValue(String value) {
        for (ScriptPurposeEnums purpose : values()) {
            if (purpose.getValue().equals(value)) {
                return purpose;
            }
        }
        throw new IllegalArgumentException("未知的ScriptPurpose: " + value);
    }

    // 如果你需要通过label获取对应的ScriptPurpose枚举值，可以添加如下方法
    public static ScriptPurposeEnums fromLabel(String label) {
        for (ScriptPurposeEnums purpose : values()) {
            if (purpose.getLabel().equals(label)) {
                return purpose;
            }
        }
        throw new IllegalArgumentException("未知的ScriptPurpose: " + label);
    }
}