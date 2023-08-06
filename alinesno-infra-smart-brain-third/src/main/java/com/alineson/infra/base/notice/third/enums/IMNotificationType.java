package com.alineson.infra.base.notice.third.enums;

public enum IMNotificationType {
    /** 钉钉通知 */
    DINGTALK("钉钉通知"),
    /** 微信通知 */
    WECHAT("微信通知"),
    /** 企业微信通知 */
    ENTERPRISE_WECHAT("企业微信通知"),
    /** 飞书通知 */
    FEISHU("飞书通知");

    private String type;

    IMNotificationType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
