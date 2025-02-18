package com.alinesno.infra.base.search.memory.enums;

public enum MessageRoleEnum {
    /**
     * 表示由用户发送的消息。
     */
    USER("user"),

    /**
     * 表示由助手响应或执行的动作。
     */
    ASSISTANT("assistant"),

    /**
     * 表示系统级别的消息或动作。
     */
    SYSTEM("system");

    // 关联的字符串值
    private final String role;

    // 构造函数，用于初始化枚举成员的关联字符串值
    MessageRoleEnum(String role) {
        this.role = role;
    }

    // 重写toString方法，使得返回的是关联的字符串值，而不是默认的枚举名称
    @Override
    public String toString() {
        return this.role;
    }
}