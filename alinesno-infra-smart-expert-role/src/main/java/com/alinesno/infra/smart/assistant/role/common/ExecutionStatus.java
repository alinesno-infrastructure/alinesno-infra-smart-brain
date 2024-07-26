package com.alinesno.infra.smart.assistant.role.common;

import lombok.Getter;

@Getter
public enum ExecutionStatus {
    SUCCESS("success"),
    FAIL("fail");

    private final String status;

    ExecutionStatus(String status) {
        this.status = status;
    }

}
