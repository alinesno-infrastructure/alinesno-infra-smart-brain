package com.alinesno.infra.smart.scene.enums;

import lombok.Getter;

@Getter
public enum TaskStatusEnum {

    NOT_RUN(0, "未运行"),
    RUN_COMPLETED(1, "运行完成"),
    RUNNING(2, "运行中"),
    GENERATING_CHAPTERS(3, "正在生成章节"),
    CANCELLED(8, "已删除"),
    RUN_FAILED(9, "运行失败");

    private final int code;
    private final String description;

    TaskStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static TaskStatusEnum getByCode(int code) {
        for (TaskStatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}