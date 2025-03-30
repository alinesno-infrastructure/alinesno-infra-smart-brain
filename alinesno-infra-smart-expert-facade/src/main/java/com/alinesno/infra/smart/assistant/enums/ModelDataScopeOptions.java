package com.alinesno.infra.smart.assistant.enums;

import lombok.Getter;

@Getter
public enum ModelDataScopeOptions {

    PERSON("person", "person", "私有", "此数据仅你个人可见和使用"),
    ORG("org", "org", "组织", "此数据可在组织内部共享和使用"),
    PUBLIC("public", "public", "公开", "此数据可被所有人访问和查看");

    private final String value;
    private final String label;
    private final String text;
    private final String desc;

    ModelDataScopeOptions(String value, String label, String text, String desc) {
        this.value = value;
        this.label = label;
        this.text = text;
        this.desc = desc;
    }
}    