package com.alinesno.infra.base.search.enums;

import lombok.Getter;

// 文档状态枚举类
@Getter
public enum DocumentStatusEnums {

    NOT_UPLOADED("not_uploaded", "未上传"),
    UPLOADING("uploading", "上传中"),
    UPLOADED("uploaded", "已上传"),
    IMPORTING("importing", "导入中"),
    // 未导入
    IMPORT_NOT_COMPLETED("import_not_completed", "未导入"),
    IMPORT_COMPLETED("import_completed", "导入完成"),
    IMPORT_FAILED("import_failed", "导入失败");

    private final String code;
    private final String description;

    DocumentStatusEnums(String code, String description) {
        this.code = code;
        this.description = description;
    }
}