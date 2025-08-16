package com.alinesno.infra.smart.assistant.point.processor;

import com.alinesno.infra.smart.point.annotation.ScenePointAnnotation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;

@Slf4j
public class ScenePointProcessor extends PointProcessorParent {

    private final ScenePointAnnotation annotation;
    private final Method method;
    private double currentRateLimit; // 当前限流值

    private Long userId ;
    private Long orgId ;

    public ScenePointProcessor(Method method, ScenePointAnnotation scenePointAnnotation) {
        super();
        this.method = method;
        this.annotation = AnnotationUtils.findAnnotation(method, ScenePointAnnotation.class);
    }

    public boolean checkRateLimit(Long userId, Long orgId) {
        return super.checkRateLimit(orgId, userId , currentRateLimit);
    }

    @Override
    public void process(HttpServletRequest request, Long userId , Long orgId) throws Exception {
        if (annotation == null) {
            return;
        }

        this.userId = userId ;
        this.orgId = orgId ;
        log.debug("处理ScenePoint注解 - userId: {}", userId);
        
        // 这里可以继续处理ScenePoint相关的积分逻辑
    }

    /**
     * 扣除积分
     * @param userId
     * @param orgId
     */
    public void deductPoints(Long userId, Long orgId) {
    }
}