package com.alinesno.infra.base.search.enums;

import lombok.Getter;

@Getter
public enum ConfigTypeEnums {
    
    DEFAULT("default", "default", "默认", "将使用系统默认的模型配置"),
    CUSTOM("constom", "constom", "高级", "通过高级配置可以自定义识别模型");
    
    private final String value;
    private final String label;
    private final String text;
    private final String desc;
    
    ConfigTypeEnums(String value, String label, String text, String desc) {
        this.value = value;
        this.label = label;
        this.text = text;
        this.desc = desc;
    }

    // 根据value查找枚举
    public static ConfigTypeEnums fromValue(String value) {
        for (ConfigTypeEnums type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的配置类型: " + value);
    }
    
    // 根据label查找枚举
    public static ConfigTypeEnums fromLabel(String label) {
        for (ConfigTypeEnums type : values()) {
            if (type.label.equals(label)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的配置标签: " + label);
    }
}