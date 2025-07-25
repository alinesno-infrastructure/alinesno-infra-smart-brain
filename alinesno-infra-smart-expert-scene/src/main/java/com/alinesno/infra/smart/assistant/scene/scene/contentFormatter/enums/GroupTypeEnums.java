package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.enums;

import lombok.Getter;

/**
 * 分组类型枚举
 */
@Getter
public enum GroupTypeEnums {

    AUDIT("audit", "审核"),
    LAYOUT("layout", "排版") ;

    private final String value;
    private final String label;

    GroupTypeEnums(String value, String label) {
        this.value = value;
        this.label = label;
    }

}
