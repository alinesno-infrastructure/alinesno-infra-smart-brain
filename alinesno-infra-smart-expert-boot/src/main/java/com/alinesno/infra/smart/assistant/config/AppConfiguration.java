package com.alinesno.infra.smart.assistant.config;

import com.alinesno.infra.common.facade.enable.EnableActable;
import com.alinesno.infra.common.web.adapter.sso.enable.EnableInfraSsoApi;
import com.alinesno.infra.common.web.log.aspect.LogAspect;
import com.dtflys.forest.springboot.annotation.ForestScan;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 统一配置中心
 */
@Slf4j
@EnableScheduling
@EnableRetry
@EnableInfraSsoApi
@EnableActable
@Configuration
@ForestScan(basePackages = "com.alinesno.infra.smart.assistant.adapter")
@MapperScan({"com.alinesno.infra.smart.assistant.mapper" , "com.alinesno.infra.smart.brain.mapper"})
@ComponentScan({"com.alinesno.infra.smart.assistant","com.alinesno.infra.smart.brain"})
public class AppConfiguration implements CommandLineRunner {{

}
    @Bean
    public LogAspect getLogAspect() {
        return new LogAspect();
    }

    @Override
    public void run(String... args) throws Exception {
        log.debug("项目启动初始化");
    }

}
