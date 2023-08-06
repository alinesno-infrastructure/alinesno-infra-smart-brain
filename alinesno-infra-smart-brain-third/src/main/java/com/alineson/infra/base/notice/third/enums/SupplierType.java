package com.alineson.infra.base.notice.third.enums;

public enum SupplierType {
    /** 阿里云*/
    ALIBABA("阿里云短信"),
    /** 华为云*/
    HUAWEI("华为云短信"),
    /** 云片*/
    YUNPIAN("云片短信"),
    /** 腾讯云*/
    TENCENT("腾讯云短信"),
    /** 合一短信*/
    UNI_SMS("合一短信"),
    /** 京东云短信 */
    JD_CLOUD("京东云短信"),
    /** 容联云短信 */
    CLOOPEN("容联云短信"),
    /** 亿美软通短信 */
    EMAY("亿美软通短信"),
    /** 天翼云 */
    CTYUN("天翼云短信"),
    /** 网易云短信 */
    NETEASE("网易云短信");

    private String type ;

    SupplierType(String type) {
        this.type = type ;
    }

    public String getType() {
        return type;
    }

}
