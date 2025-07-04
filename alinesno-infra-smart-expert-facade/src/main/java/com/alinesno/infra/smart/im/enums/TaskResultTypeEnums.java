package com.alinesno.infra.smart.im.enums;

import lombok.Getter;

@Getter
public enum TaskResultTypeEnums {

    SUMMARY("summary", "总结"),
    REACT("react", "响应") ;

    private final String code;
    private final String name;

    TaskResultTypeEnums(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
