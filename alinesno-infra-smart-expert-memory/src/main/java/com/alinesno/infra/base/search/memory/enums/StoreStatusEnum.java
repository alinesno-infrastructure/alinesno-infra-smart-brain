package com.alinesno.infra.base.search.memory.enums;

public enum StoreStatusEnum {
    /**
     * 表示存储状态有效。
     */
    VALID("valid"),

    /**
     * 表示存储状态已过期。
     */
    EXPIRED("expired");

    // 关联的字符串值
    private final String status;

    // 构造函数，用于初始化枚举成员的关联字符串值
    StoreStatusEnum(String status) {
        this.status = status;
    }

    // 重写toString方法，使得返回的是关联的字符串值，而不是默认的枚举名称
    @Override
    public String toString() {
        return this.status;
    }
}