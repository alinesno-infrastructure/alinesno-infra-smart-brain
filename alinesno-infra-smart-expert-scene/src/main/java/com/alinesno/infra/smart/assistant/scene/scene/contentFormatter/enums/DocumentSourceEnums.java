package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.enums;

import lombok.Getter;

/**
 * 文档来源枚举
 */
@Getter
public enum DocumentSourceEnums {
    NEW("new", "新建") ,
    UPLOAD("upload", "上传") ;

    private final String code;
    private final String name;

    DocumentSourceEnums(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
