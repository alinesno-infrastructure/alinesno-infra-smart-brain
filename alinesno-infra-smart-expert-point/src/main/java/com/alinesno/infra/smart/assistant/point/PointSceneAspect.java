package com.alinesno.infra.smart.assistant.point;

import cn.dev33.satoken.stp.StpUtil;
import com.alinesno.infra.common.web.adapter.login.account.CurrentAccountJwt;
import com.alinesno.infra.smart.assistant.adapter.AipPointConsumer;
import com.alinesno.infra.smart.assistant.point.processor.AgentPointProcessor;
import com.alinesno.infra.smart.assistant.point.processor.ScenePointProcessor;
import com.alinesno.infra.smart.point.annotation.AgentPointAnnotation;
import com.alinesno.infra.smart.point.annotation.ScenePointAnnotation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.lang.exception.RpcServiceRuntimeException;
import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
public class PointSceneAspect {

    @Value("${alinesno.point.enable:false}")
    private boolean pointEnable ;

    @Value("${alinesno.point.scene-max-concurrent:2}")
    private int sceneMaxConcurrent;

    // 定义切入点：同时包含两个注解的方法
    @Pointcut("@annotation(com.alinesno.infra.smart.point.annotation.ScenePointAnnotation)")
    public void pointAnnotationsPointcut() {}

    @Around("pointAnnotationsPointcut()")
    public Object aroundControllerMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!pointEnable) {
            return joinPoint.proceed(); // 直接放行
        }

        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        long startTime = System.currentTimeMillis();

        // 1. 原有鉴权逻辑
        ScenePointAnnotation scenePointAnnotation = AnnotationUtils.findAnnotation(method, ScenePointAnnotation.class);

        if(scenePointAnnotation != null){
            // 验证登录状态
            if (!StpUtil.isLogin()) {
                throw new RpcServiceRuntimeException("用户未登录");
            }

            Long orgId = CurrentAccountJwt.get().getOrgId();
            Long userId = CurrentAccountJwt.get().getId();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

            // 初始化处理器
            ScenePointProcessor sceneProcessor = new ScenePointProcessor(scenePointAnnotation) ;
            sceneProcessor.setSceneMaxConcurrent(sceneMaxConcurrent);
            sceneProcessor.process(request, userId, orgId);
        }

        return joinPoint.proceed();
    }
}