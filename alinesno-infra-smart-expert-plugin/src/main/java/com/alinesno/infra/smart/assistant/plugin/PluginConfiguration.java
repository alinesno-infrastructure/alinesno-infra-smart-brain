package com.alinesno.infra.smart.assistant.plugin;

import com.alinesno.infra.smart.assistant.plugin.loader.PluginImportBeanDefinitionRegistrar;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 插件的配置
 */
@MapperScan("com.alinesno.infra.smart.assistant.plugin.mapper")
@Import(PluginImportBeanDefinitionRegistrar.class)
@Configuration
public class PluginConfiguration {
}
