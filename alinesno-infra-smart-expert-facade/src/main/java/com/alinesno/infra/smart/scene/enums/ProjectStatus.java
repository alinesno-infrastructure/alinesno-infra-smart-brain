package com.alinesno.infra.smart.scene.enums;

import lombok.Getter;

@Getter
public enum ProjectStatus {
    QUEUED("排队中"),
    PROCESSING("处理中"),
    COMPLETED("已完成"),
    FAILED("失败"),
    RETRYING("重试中"),
    CANCELED("已取消");

    private final String desc;

    ProjectStatus(String desc) {
        this.desc = desc;
    }

}