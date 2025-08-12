//package com.alinesno.infra.smart.assistant.point.config;
//
//import com.alinesno.infra.smart.assistant.point.PointInterceptor;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class PointWebConfig implements WebMvcConfigurer {
//
//    private final PointInterceptor pointInterceptor;
//
//    // 通过构造器注入 PointInterceptor（推荐方式）
//    public PointWebConfig(PointInterceptor pointInterceptor) {
//        this.pointInterceptor = pointInterceptor;
//    }
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(pointInterceptor) // 使用注入的拦截器实例
//                .addPathPatterns("/**") // 拦截所有路径
//                .excludePathPatterns("/login", "/static/**"); // 排除登录接口和静态资源
//    }
//}