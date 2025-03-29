package com.alinesno.infra.smart.im.enums;

import lombok.Getter;

@Getter
public enum PushOptions {

    SHOP("shop", "推送商店", "角色只可被组织引用不可修改"),
    MARKET("market", "推送市场", "角色能复制修改并单独定义");

    private final String value;
    private final String label;
    private final String description;

    PushOptions(String value, String label, String description) {
        this.value = value;
        this.label = label;
        this.description = description;
    }
}    