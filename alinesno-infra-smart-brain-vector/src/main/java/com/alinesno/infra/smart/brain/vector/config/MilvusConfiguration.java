package com.alinesno.infra.smart.brain.vector.config;

import io.milvus.client.MilvusServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * 此处功能代码参考 <a href="https://juejin.cn/post/7251501842986762301">SpringBoot整合Milvus</a>
 */
@Configuration
public class MilvusConfiguration {

    /**
     *  milvus ip addr
     */
    @Value("${alinesno.infra.smart.milvus.ip-addr:127.0.0.1}")
    private String ipAddr;

    /**
     * milvus   port
     */
    @Value("${alinesno.infra.smart.milvus.port:19530}")
    private Integer  port;

    @Bean
    @Scope("singleton")
    public MilvusServiceClient getMilvusClient() {
        return getMilvusFactory().getMilvusClient();
    }

    @Bean(initMethod = "init", destroyMethod = "close")
    public MilvusRestClientFactory getMilvusFactory() {
        return  MilvusRestClientFactory.build(ipAddr, port);
    }
}