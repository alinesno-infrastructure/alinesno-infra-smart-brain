package com.alinesno.infra.smart.scene.enums;

import lombok.Getter;

@Getter
public enum TaskStatusEnum {

    NOT_RUN("not_run", "未运行"),
    RUN_COMPLETED("completed", "运行完成"),
    RUNNING("running", "运行中"),
    GENERATING_CHAPTERS("generating", "正在生成章节"),
    CANCELLING("cancelling", "取消中"),
    CANCELLED("cancelled", "已删除"),
    RUN_FAILED("failed", "运行失败");

    private final String code;
    private final String description;

    TaskStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static TaskStatusEnum getByCode(String code) {
        for (TaskStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}