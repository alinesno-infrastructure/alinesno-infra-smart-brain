package com.alinesno.infra.smart.assistant.role.common;

import lombok.Getter;

@Getter
public enum WorkflowStatusEnum {

    COMPLETED("Completed"), // 完成
    FAILED("Failed"),       // 失败
    IN_PROGRESS("In Progress"), // 进行中
    PENDING("Pending");     // 待处理

    private final String status;

    WorkflowStatusEnum(String status) {
        this.status = status;
    }

}
