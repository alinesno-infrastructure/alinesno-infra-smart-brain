package com.alineson.infra.base.notice.third.enums;

public enum EmailSupplierType {
    /** 163邮箱 */
    EMAIL_163("163邮箱"),
    /** 阿里云邮箱 */
    EMAIL_ALIYUN("阿里云邮箱"),
    /** 企业自定义邮箱 */
    EMAIL_CUSTOM("企业自定义邮箱"),
    /** 腾讯云邮箱 */
    EMAIL_TENCENT("腾讯云邮箱");

    private String type;

    EmailSupplierType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
