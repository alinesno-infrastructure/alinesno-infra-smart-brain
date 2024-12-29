package com.alinesno.infra.smart.assistant.screen.enums;

import lombok.Getter;

/**
 * ScreenTypeEnums 枚举类
 */
@Getter
public enum ScreenTypeEnums {

    LARGE_TEXT("large_text","大文本"),
    EXAM("exam","培训考试"),
    LEADER_MODEL("leader_model","管理者");

    private final String key;
    private final String name;

    private ScreenTypeEnums(String key, String name) {
        this.key = key;
        this.name = name;
    }

    /**
     * 通过key获取到类型
     */
    public static ScreenTypeEnums getByKey(String key) {
        for (ScreenTypeEnums type : values()) {
            if (type.getKey().equals(key)) {
                return type;
            }
        }
        return null;
    }

}
