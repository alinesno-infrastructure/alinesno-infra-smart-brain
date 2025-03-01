package com.alinesno.infra.smart.assistant.workflow.enums;

/**
 * 工作流发布状态枚举类
 */
public enum PublishStatus {
    UNPUBLISHED("unpublished", "未发布"),
    PUBLISHED("published", "已发布");

    private final String code;
    private final String label;

    PublishStatus(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    /**
     * 根据 code 获取对应的枚举实例
     * @param code 状态码
     * @return 对应的 PublishStatus 枚举实例，如果未找到则返回 null
     */
    public static PublishStatus getByCode(String code) {
        for (PublishStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}