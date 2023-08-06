package com.alineson.infra.smart.brain.api.interceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * MVC 配置类，用于配置拦截器和其他相关设置。
 *
 * <p>作者：luoxiaodong</p>
 *@version 1.0.0
 */
@SuppressWarnings("deprecation")
@Configuration
public class MVCConfig extends WebMvcConfigurationSupport {

    /**
     * 创建 GPT 拦截器的 Bean 实例。
     *
     * @return GPTInterceptor 实例
     */
    @Bean
    public GPTInterceptor securityInterceptor() {
        return new GPTInterceptor();
    }

    /**
     * 添加拦截器到拦截器注册表中。
     *
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(securityInterceptor())
                .excludePathPatterns("/static/*")
                .excludePathPatterns("/error")
                .addPathPatterns("/**");
    }

}
