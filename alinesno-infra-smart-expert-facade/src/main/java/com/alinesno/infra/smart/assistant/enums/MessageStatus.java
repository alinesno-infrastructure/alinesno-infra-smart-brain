package com.alinesno.infra.smart.assistant.enums;

import lombok.Getter;

/**
 * 表示消息的状态枚举类。
 */
@Getter
public enum MessageStatus {

    PENDING("准备构建", "pending"),    // 准备构建
    SENT("构建中", "sent"),          // 构建中
    SUCCESS("构建成功", "success"),          // 构建成功
    FAILED("构建失败", "failed");    // 构建失败

    private final String label;
    private final String value;

    // 构造函数
    MessageStatus(String label, String value) {
        this.label = label;
        this.value = value;
    }

}
