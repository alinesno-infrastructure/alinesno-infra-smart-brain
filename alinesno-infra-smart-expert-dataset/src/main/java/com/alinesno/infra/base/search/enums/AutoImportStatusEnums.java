package com.alinesno.infra.base.search.enums;

import lombok.Getter;

/**
 * 数据集是否自动导入的状态枚举类
 */
@Getter
public enum AutoImportStatusEnums {

    AUTO_IMPORT(1, "自动导入"),
    MANUAL_IMPORT(0, "手动导入");

    private final Integer code;
    private final String description;

    AutoImportStatusEnums(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

}