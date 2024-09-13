package com.alinesno.infra.smart.assistant.config;

import com.alinesno.infra.common.facade.enable.EnableActable;
import com.alinesno.infra.common.web.adapter.sso.enable.EnableInfraSsoApi;
import com.alinesno.infra.common.web.log.aspect.LogAspect;
import com.alinesno.infra.smart.assistant.initialize.IAssistantInitService;
import com.dtflys.forest.springboot.annotation.ForestScan;
import jakarta.servlet.MultipartConfigElement;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.unit.DataSize;

/**
 * 统一配置中心
 */
@Slf4j
@EnableScheduling
@EnableRetry
@EnableInfraSsoApi
@EnableActable
@ServletComponentScan(basePackages = {"com.alinesno.infra.smart.brain"})
@Configuration
@ForestScan(basePackages = "com.alinesno.infra.smart.assistant.adapter")
@MapperScan({
        "com.alinesno.infra.smart.assistant.mapper" ,
        "com.alinesno.infra.base.im.mapper" ,
        "com.alinesno.infra.smart.brain.mapper" ,
        "com.alinesno.infra.smart.inference.mapper"
})
@ComponentScan({
        "com.alinesno.infra.smart.assistant",
        "com.alinesno.infra.smart.brain" ,
        "com.alinesno.infra.base.im" ,
        "com.alinesno.infra.smart.inference"
})
public class AppConfiguration implements CommandLineRunner {

    @Autowired
    private IAssistantInitService assistantInitService ;

    @Bean
    public MultipartConfigElement getMultipartConfig() {
        MultipartConfigFactory config = new MultipartConfigFactory() ;
        config.setMaxFileSize(DataSize.parse("20MB")); 		// 设置上传文件的单个大小限制
        config.setMaxRequestSize(DataSize.parse("100MB")); 		// 设置总的上传的大小限制
        config.setLocation(System.getProperty("java.io.tmpdir")); 	// 设置临时保存目录
        return config.createMultipartConfig() ;	// 创建一个上传配置
    }

    @Bean
    public LogAspect getLogAspect() {
        return new LogAspect();
    }

    @Override
    public void run(String... args) throws Exception {
        log.debug("项目启动初始化");

        // 初始化示例分类
        assistantInitService.expertCatalog();

        // 初始化示例专家分类
        assistantInitService.expert();

        // 初始化插件
        assistantInitService.initPlugin();

        // 初始化示例频道
        assistantInitService.initChannel();
    }

}
