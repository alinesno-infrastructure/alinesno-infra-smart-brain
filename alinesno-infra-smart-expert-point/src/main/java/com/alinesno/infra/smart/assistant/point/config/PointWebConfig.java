package com.alinesno.infra.smart.assistant.point.config;

import com.alinesno.infra.smart.assistant.point.PointInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PointWebConfig implements WebMvcConfigurer {
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册拦截器，拦截所有请求
        registry.addInterceptor(new PointInterceptor())
                .addPathPatterns("/**") // 拦截所有路径
                .excludePathPatterns("/login", "/static/**"); // 排除登录接口和静态资源
    }
}