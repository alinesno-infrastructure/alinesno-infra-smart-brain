package com.alinesno.infra.base.notice.enums;

public enum NoticeStatus {
    SENT("已发送"),
    PENDING("待发送"),
    SENDING("发送中"),
    FAILED("发送失败");

    private String status;

    NoticeStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
