package com.alinesno.infra.base.im.enums;

import lombok.Getter;

@Getter
public enum AccountType {

    PLATFORM(0, "平台账号"),
    AGENT(1, "Agent账号"),
    OTHER(9, "其它类型");

    private final int value;
    private final String label;

    AccountType(int value, String label) {
        this.value = value;
        this.label = label;
    }

}