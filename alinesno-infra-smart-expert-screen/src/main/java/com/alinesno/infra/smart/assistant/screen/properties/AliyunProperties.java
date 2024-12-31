package com.alinesno.infra.smart.assistant.screen.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云OSS配置
 */
@Configuration
@ConfigurationProperties(prefix = "alinesno.infra.smart.brain.aliyun")
@Data
public class AliyunProperties {

    private String regionId = "cn-shanghai" ;
    private String endpoint = "http://oss-cn-shanghai.aliyuncs.com";  // Endpoint以杭州为例，其它Region请按实际情况填写。
    private String bucketName = "aip-media";  // 阿里云OSS的Bucket名称
    private String accessKeyId;  // 阿里云OSS的AccessKeyId
    private String accessKeySecret;  // 阿里云OSS的AccessKeySecret

}