package com.alinesno.infra.base.im.enums;

import lombok.Getter;

/**
 * 任务状态
 */
@Getter
public enum TaskStatusEnums {

    PENDING("pending", "开始处理"),
    COMPLETED("completed", "已完成"),
    FAILED("failed", "失败"),
    SUBMIT("submit", "提交任务"),
    HANDLE_EXCEPTION("handle_exception", "处理异常"),
    RETRY("retry", "任务重试"),
    TASK_END("task_end", "任务结束"),
    PROCESSING("processing", "处理中");

    private final String value;
    private final String label;

    TaskStatusEnums(String value, String label) {
        this.value = value;
        this.label = label;
    }

}