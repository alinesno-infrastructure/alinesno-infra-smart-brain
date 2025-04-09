package com.alinesno.infra.smart.assistant.workplace.enums;

import lombok.Getter;

/**
 * 场景来源类型
 * 后台添加/前端复制
 */
@Getter
public enum WorkplaceSourceEnums {

    BACKEND("backend", "后台添加"),
    FRONTEND("frontend", "前端复制");

    private final String code;
    private final String label;

    WorkplaceSourceEnums(String code, String label) {
        this.code = code;
        this.label = label;
    }

}
