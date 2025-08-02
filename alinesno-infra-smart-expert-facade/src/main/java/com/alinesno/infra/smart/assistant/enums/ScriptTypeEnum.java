package com.alinesno.infra.smart.assistant.enums;

import lombok.Getter;

@Getter
public enum ScriptTypeEnum {
    SCRIPT("script", "脚本"),
    REACT("react", "智能体"),
    FLOW("flow", "流程"),
    RAG("rag", "RAG知识库"),
    SIMPLE("simple", "简单"),
    DEEPSEARCH("deepsearch", "深度搜索");

    private final String code;
    private final String label;

    ScriptTypeEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }

    /**
     * 根据 code 获取对应的枚举实例
     * @param code 枚举对应的 code
     * @return 匹配的枚举实例，如果未找到则抛出 IllegalArgumentException
     */
    public static ScriptTypeEnum fromCode(String code) {
        for (ScriptTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未找到 code 为 " + code + " 对应的 ScriptTypeEnum 枚举实例");
    }
}