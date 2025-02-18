package com.alinesno.infra.base.search.memory.enums;

public enum ActionStatusEnum {
    /**
     * 表示一个新创建的节点。
     */
    NEW("new"),

    /**
     * 标识节点已被修改。
     */
    MODIFIED("modified"),

    /**
     * 指定节点的实际内容已更改。
     */
    CONTENT_MODIFIED("content_modified"),

    /**
     * 什么也不做。
     */
    NONE("none"),

    /**
     * 删除记忆。
     */
    DELETE("delete");

    // 关联的字符串值
    private final String status;

    // 构造函数，用于初始化枚举成员的关联字符串值
    ActionStatusEnum(String status) {
        this.status = status;
    }

    // 重写toString方法，使得返回的是关联的字符串值，而不是默认的枚举名称
    @Override
    public String toString() {
        return this.status;
    }
}