package com.alinesno.infra.smart.assistant.api.task;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * @ToString： 自动生成toString方法，以便于对象的字符串表示。
 * @Data： 自动生成getter和setter方法，equals和hashCode方法，以及toString方法。
 * @JsonIgnoreProperties(ignoreUnknown = true)： 当JSON中有数据库表中没有的字段时，忽略未知字段。
 */
@ToString
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParamsDto {

    private String name; // 字段名与 JSON 键对应
    private String desc; // 描述
    private boolean delivery; // 注意布尔值在 JSON 中是 true/false，在 Java 中是 boolean
    private int retryCount; // 重试次数，整型数值
    private long dataSourceId ; // 数据源ID
    private String rawScript; // 原始脚本
    private List<String> resourceId; // 资源名称
    private Map<String,String> customParams; // 自定义参数

    // Git仓库信息
    private String gitUrl; // Git仓库地址
    private String gitBranch;  // Git分支
    private String gitUsername; // Git用户名
    private String gitPassword; // Git密码

    // 企业微信群机器人Webhook地址
    private String imType;
    private String wechatKey ;
    private String email;
    private String noticeContent ;

    // Maven项目配置信息
    private String pomXml ;
    private String goals ;
    private String settings ;

    // HTTP请求信息
    private String method ;
    private String requestBody ;
    private String url ;

    // ANSIBLE配置信息
    private String inventory ;
    private String extraVars ;
}
