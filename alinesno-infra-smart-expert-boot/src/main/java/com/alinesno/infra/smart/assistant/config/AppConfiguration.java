package com.alinesno.infra.smart.assistant.config;

import com.alinesno.infra.base.search.memory.BaseMemoryStore;
import com.alinesno.infra.base.search.service.IVectorDatasetService;
import com.alinesno.infra.base.search.vector.service.IPgVectorService;
import com.alinesno.infra.common.facade.enable.EnableActable;
import com.alinesno.infra.common.security.api.enable.EnableInfraApiSecurity;
import com.alinesno.infra.common.web.adapter.sso.enable.EnableInfraSsoApi;
import com.alinesno.infra.common.web.log.aspect.LogAspect;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.agent.DeepSearchService;
import com.dtflys.forest.springboot.annotation.ForestScan;
import jakarta.servlet.MultipartConfigElement;
import lombok.extern.slf4j.Slf4j;
import org.dromara.x.file.storage.spring.EnableFileStorage;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.unit.DataSize;

/**
 * 统一配置中心
 */
@Slf4j
@EnableFileStorage
@EnableScheduling
@EnableRetry
@EnableAsync
@EnableInfraSsoApi
@EnableActable
@ServletComponentScan(basePackages = {"com.alinesno.infra.smart.brain"})
@Configuration
@ForestScan({
        "com.alinesno.infra.common.web.adapter.base.consumer" ,
        "com.alinesno.infra.smart.assistant.adapter"
})
@MapperScan({
        "com.alinesno.infra.smart.assistant.mapper" ,
        "com.alinesno.infra.base.im.mapper" ,
        "com.alinesno.infra.smart.brain.mapper" ,
        "com.alinesno.infra.smart.assistant.template.mapper" ,
        "com.alinesno.infra.smart.assistant.scene.**.mapper" ,
        "com.alinesno.infra.smart.assistant.workplace.mapper" ,
        "com.alinesno.infra.smart.assistant.plugin.mapper" ,
        "com.alinesno.infra.smart.inference.mapper",
        "com.alinesno.infra.base.search.mapper",
        "com.alinesno.infra.smart.assistant.publish.mapper",
        "com.alinesno.infra.smart.assistant.workflow.mapper"
})
@ComponentScan({
        "com.alinesno.infra.smart.assistant",
        "com.alinesno.infra.smart.brain" ,
        "com.alinesno.infra.base.im" ,
        "com.alinesno.infra.base.search",
        "com.alinesno.infra.common.extend.datasource" ,
        "com.alinesno.infra.smart.inference"
})
@EnableInfraApiSecurity
public class AppConfiguration implements CommandLineRunner {

    @Autowired
    private IVectorDatasetService vectorDatasetService ;

    @Autowired
    private IPgVectorService pgVectorService ;

    @Autowired
    private BaseMemoryStore baseMemoryStore ;

    @Autowired
    private DeepSearchService deepSearchService;

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
        log.debug("deepSearchService = {}" , deepSearchService);
        baseMemoryStore.createMemoryVectorIndex();
        log.info("当前向量存储引擎:{}" , vectorDatasetService.getVectorEngine());
        pgVectorService.createVectorIndex(null);
        log.debug("项目启动完成");
    }

}
