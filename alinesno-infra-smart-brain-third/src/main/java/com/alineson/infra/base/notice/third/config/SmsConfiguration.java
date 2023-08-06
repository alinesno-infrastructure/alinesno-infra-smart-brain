package com.alineson.infra.base.notice.third.config;

import org.dromara.sms4j.aliyun.config.AlibabaConfig;
import org.dromara.sms4j.core.config.SupplierFactory;
import org.dromara.sms4j.ctyun.config.CtyunConfig;
import org.dromara.sms4j.huawei.config.HuaweiConfig;
import org.dromara.sms4j.jdcloud.config.JdCloudConfig;
import org.dromara.sms4j.netease.config.NeteaseConfig;
import org.dromara.sms4j.tencent.config.TencentConfig;
import org.dromara.sms4j.unisms.config.UniConfig;
import org.springframework.context.annotation.Bean;

/**
 * //阿里云短信配置
 * AlibabaConfig.builder();
 * //华为云短信配置
 * HuaweiConfig.builder();
 * //unisms短信配置
 * UniConfig.builder();
 * //京东短信配置
 * JdCloudConfig.builder();
 * //腾讯短信配置
 * TencentConfig.builder();
 * //云片短信配置
 * YunpianConfig.builder();
 * //容联云短信配置
 * CloopenConfig.builder();
 * //亿美软通配置
 * EmayConfig.builder();
 * //天翼云配置
 * CtyunConfig.builder();
 */
public class SmsConfiguration {
    //unisms短信差异化配置
    public void setConfig(){
        //以下空字符串仅为演示使用，实际项目可以通过各种途径获取相应的数据
        UniConfig uni = UniConfig.builder()
                .accessKeyId("")
                .templateId("")
                .templateName("")
                .signature("")
                .build();
        SupplierFactory.setUniConfig(uni);
    }

    @Bean
    public void setConfiguration(){

        // config alibaba sms
        AlibabaConfig alibabaConfig = SupplierFactory.getAlibabaConfig();
        alibabaConfig.setAccessKeyId("您的accessKey");
        alibabaConfig.setAccessKeySecret("您的accessKeySecret");
        alibabaConfig.setSignature("测试签名");
        alibabaConfig.setTemplateId("SM123581321");
        alibabaConfig.setTemplateName("code");
        alibabaConfig.setAction("SendSms");
        alibabaConfig.setVersion("2017-05-25");
        alibabaConfig.setRegionId("cn-hangzhou");

        // config tencent sms
        TencentConfig tencentConfig = SupplierFactory.getTencentConfig();
        tencentConfig.setAccessKeyId("your accessKeyId");
        tencentConfig.setAccessKeySecret("your accessKeySecret");
        tencentConfig.setSdkAppId("your sdkAppId");
        tencentConfig.setSignature("短信签名");
        tencentConfig.setTemplateId("your TemplateId");
        tencentConfig.setTerritory("地域信息");
        tencentConfig.setConnTimeout(60);
        tencentConfig.setRequestUrl("sms.tencentcloudapi.com");
        tencentConfig.setAction("SendSms");
        tencentConfig.setVersion("2021-01-11");

        // tianyi
        CtyunConfig ctyunConfig = SupplierFactory.getCtyunConfig();
        ctyunConfig.setAccessKeyId("您的accessKey");
        ctyunConfig.setAccessKeySecret("您的accessKeySecret");
        ctyunConfig.setSignature("测试签名");
        ctyunConfig.setTemplateId("SM123581321");
        ctyunConfig.setTemplateName("code");
        ctyunConfig.setAction("SendSms");
    }

    @Bean
    public HuaweiConfig huaweiConfig(){
        HuaweiConfig huaweiConfig = SupplierFactory.getHuaweiConfig();
        huaweiConfig.setAppKey("your appKey");
        huaweiConfig.setAppSecret("your appSecret");
        huaweiConfig.setSignature("your signature");
        huaweiConfig.setSender("your sender");
        huaweiConfig.setTemplateId("your templateId");
        huaweiConfig.setStatusCallBack("状态回调地址");
        huaweiConfig.setUrl("官方创建短信应用后生成的地址");
        return huaweiConfig;
    }

    @Bean
    public JdCloudConfig jdCloudConfig(){
        JdCloudConfig jdCloudConfig = SupplierFactory.getJdCloudConfig();
        jdCloudConfig.setAccessKeyId("your accessKeyId");
        jdCloudConfig.setAccessKeySecret("your accessKeySecret");
        jdCloudConfig.setSignature("your signature");
        jdCloudConfig.setTemplateId("your templateId");
        jdCloudConfig.setRegion("地域信息");
        return jdCloudConfig;
    }

    @Bean
    public NeteaseConfig neteaseConfig() {
        NeteaseConfig neteaseConfig = SupplierFactory.getNeteaseConfig();
        neteaseConfig.setAccessKeyId("your accessKeyId");
        neteaseConfig.setAccessKeySecret("your accessKeySecret");
        neteaseConfig.setSignature("your signature");
        neteaseConfig.setTemplateId("your templateId");
        neteaseConfig.setTemplateName("your templateName");
        neteaseConfig.setTemplateUrl("your TemplateUrl");
        neteaseConfig.setCodeUrl("your CodeUrl");
        neteaseConfig.setVerifyUrl("your VerifyUrl");
        return neteaseConfig;
    }

}
