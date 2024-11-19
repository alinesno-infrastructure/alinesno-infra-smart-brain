package com.alinesno.infra.smart.assistant.enums;

import lombok.Getter;

@Getter
public enum ToolTypeEnums {

    UTILITY_TOOLS("utility-tools", "实用工具"),
    WEB_SEARCH("web-search", "网页搜索"),
    PHOTO_PHOTOGRAPHY("photo-photography", "照片摄影"),
    SOCIAL("social", "社交"),
    PROJECT_MANAGEMENT("project-management", "项目管理"),
    SECURITY_CONTROL("security-control", "安全管控");

    private final String key;
    private final String name;

    ToolTypeEnums(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public static ToolTypeEnums fromKey(String key) {
        for (ToolTypeEnums type : ToolTypeEnums.values()) {
            if (type.getKey().equalsIgnoreCase(key)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown tool type key: " + key);
    }
}