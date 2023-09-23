package com.alinesno.infra.smart.brain.enums;

public enum TaskStatus {

    QUEUED(0, "排队中"),
    RUNNING(1, "运行中"),
    COMPLETED(2, "已完成"),
    FAILED(9, "失败");

    private final int value;
    private final String label;

    TaskStatus(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public int getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    public static TaskStatus fromValue(int value) {
        for (TaskStatus status : TaskStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid task status value: " + value);
    }
}
