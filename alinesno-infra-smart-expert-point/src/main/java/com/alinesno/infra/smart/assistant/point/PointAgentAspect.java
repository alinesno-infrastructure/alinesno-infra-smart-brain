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
import org.springframework.web.context.request.async.DeferredResult;

import javax.lang.exception.RpcServiceRuntimeException;
import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
public class PointAgentAspect {

    @Value("${alinesno.point.enable:false}")
    private boolean pointEnable ;

    @Value("${alinesno.point.agent-max-concurrent:5}") // 默认值5
    private int agentMaxConcurrent;

    // 定义切入点：同时包含两个注解的方法
    @Pointcut("@annotation(com.alinesno.infra.smart.point.annotation.AgentPointAnnotation)")
    public void pointAnnotationsPointcut() {}

    @Around("pointAnnotationsPointcut()")
    public Object aroundControllerMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!pointEnable) {
            return joinPoint.proceed(); // 直接放行
        }

        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        AgentPointAnnotation agentPointAnnotation = AnnotationUtils.findAnnotation(method, AgentPointAnnotation.class);

        if(agentPointAnnotation != null){
            // 验证登录状态
            if (!StpUtil.isLogin()) {
                throw new RpcServiceRuntimeException("用户未登录");
            }

            Long orgId = CurrentAccountJwt.get().getOrgId();
            Long userId = CurrentAccountJwt.get().getId();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

            // 初始化处理器
            AgentPointProcessor agentProcessor = new AgentPointProcessor(agentPointAnnotation);
            agentProcessor.setAgentMaxConcurrent(agentMaxConcurrent);
            agentProcessor.process(request, userId, orgId);
        }

        return joinPoint.proceed();
    }
}