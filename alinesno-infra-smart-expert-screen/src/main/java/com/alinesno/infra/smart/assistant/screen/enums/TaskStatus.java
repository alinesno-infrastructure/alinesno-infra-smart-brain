package com.alinesno.infra.smart.assistant.screen.enums;

import lombok.Getter;

@Getter
public enum TaskStatus {

    NOT_STARTED(0, "未开始"),
    IN_PROGRESS(1, "执行中"),
    COMPLETED(2, "结束"),
    FAILED(9, "失败");

    private final int code;
    private final String description;

    TaskStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static TaskStatus fromCode(int code) {
        for (TaskStatus status : TaskStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid task status code: " + code);
    }

    @Override
    public String toString() {
        return "TaskStatus{" +
                "code=" + code +
                ", description='" + description + '\'' +
                '}';
    }
}