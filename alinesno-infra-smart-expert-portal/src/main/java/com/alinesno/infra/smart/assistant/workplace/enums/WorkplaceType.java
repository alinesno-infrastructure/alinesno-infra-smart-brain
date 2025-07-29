package com.alinesno.infra.smart.assistant.workplace.enums;

import lombok.Getter;

import java.util.Objects;

@Getter
public enum WorkplaceType {

    PRIVATE("private", "个人工作台", "fa-solid fa-user", "个人工作台只能选择个人智能体、个人频道、个人场景"),
    PUBLIC("public", "公开工作台", "fa-solid fa-globe", "公开工作台只能选择公共智能体、公开频道、公开场景"),
    ORG("org", "组织工作台", "fa-solid fa-truck-plane", "组织工作台可以选择公共和组织内的智能体、频道和场景");

    private final String id;
    private final String name;
    private final String icon;
    private final String desc;

    WorkplaceType(String id, String name, String icon, String desc) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.desc = desc;
    }

    public static WorkplaceType fromId(String id) {
        for (WorkplaceType type : WorkplaceType.values()) {
            if (Objects.equals(type.getId(), id)) {
                return type;
            }
        }
        return null;
    }
}    