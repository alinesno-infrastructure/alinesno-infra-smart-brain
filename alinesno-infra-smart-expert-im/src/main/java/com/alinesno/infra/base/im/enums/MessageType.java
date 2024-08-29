package com.alinesno.infra.base.im.enums;

import lombok.Getter;

@Getter
public enum MessageType {

    BUSINESS("business", "业务标识"),
    MENTION("mention", "提及用户消息"),
    TEXT_FILE("text", "文本文件消息");

    private final String value;
    private final String label;

    MessageType(String value, String label) {
        this.value = value;
        this.label = label;
    }

    // 可以根据值获取对应的 MessageType 枚举
    public static MessageType fromValue(String value) {
        for (MessageType messageType : MessageType.values()) {
            if (messageType.value.equals(value)) {
                return messageType;
            }
        }
        throw new IllegalArgumentException("Invalid MessageType value: " + value);
    }

}
