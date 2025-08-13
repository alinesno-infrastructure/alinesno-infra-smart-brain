package com.alinesno.infra.smart.assistant.point.processor;

import com.alinesno.infra.smart.point.annotation.ScenePointAnnotation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;

@Slf4j
public class ScenePointProcessor implements PointAnnotationProcessor {

    private final ScenePointAnnotation annotation;
    private final Method method;

    public ScenePointProcessor(Method method) {
        this.method = method;
        this.annotation = AnnotationUtils.findAnnotation(method, ScenePointAnnotation.class);
    }

    @Override
    public void process(HttpServletRequest request, Long userId) throws Exception {
        if (annotation == null) {
            return;
        }

        log.debug("处理ScenePoint注解 - userId: {}", userId);
        
        // 这里可以继续处理ScenePoint相关的积分逻辑
    }
}