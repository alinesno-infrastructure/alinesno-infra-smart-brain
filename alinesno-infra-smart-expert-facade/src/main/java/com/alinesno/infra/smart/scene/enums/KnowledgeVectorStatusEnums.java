package com.alinesno.infra.smart.scene.enums;

import lombok.Getter;

/**
 * 知识向量状态枚举
 */
@Getter
public enum KnowledgeVectorStatusEnums {

    // 0导入中|1导入成功|2导入失败

    IMPORTING(0, "导入中"),
    IMPORT_SUCCESS(1, "导入成功"),
    IMPORT_FAIL(2, "导入失败");

    private final int code;
    private final String message;

    KnowledgeVectorStatusEnums(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
