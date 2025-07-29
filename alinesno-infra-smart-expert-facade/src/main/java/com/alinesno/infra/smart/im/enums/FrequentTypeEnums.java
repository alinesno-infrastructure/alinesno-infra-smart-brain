package com.alinesno.infra.smart.im.enums;

import lombok.Getter;

@Getter
public enum FrequentTypeEnums {

    AGENT("agent"),
    CHANNEL("channel"),
    SCENE("scene");

    private final String type;

    FrequentTypeEnums(String type) {
        this.type = type;
    }
}
