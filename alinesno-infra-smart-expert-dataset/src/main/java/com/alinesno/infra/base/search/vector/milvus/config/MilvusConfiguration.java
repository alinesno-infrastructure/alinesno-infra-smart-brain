package com.alinesno.infra.base.search.vector.milvus.config;

import io.milvus.client.MilvusServiceClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 此处功能代码参考 <a href="https://juejin.cn/post/7251501842986762301">SpringBoot整合Milvus</a>
 */
@Configuration
@Data
@Component
public class MilvusConfiguration {

    /**
     *  milvus ip addr
     */
    @Value("${alinesno.base.search.milvus.host:127.0.0.1}")
    private String ipAddr;

    /**
     * milvus   port
     */
    @Value("${alinesno.base.search.milvus.port:12345}")
    private Integer port;

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