package com.alinesno.infra.smart.assistant.point;

import cn.dev33.satoken.stp.StpUtil;
import com.alinesno.infra.smart.assistant.adapter.AipPointConsumer;
import com.alinesno.infra.smart.assistant.point.processor.AgentPointProcessor;
import com.alinesno.infra.smart.assistant.point.processor.ScenePointProcessor;
import com.alinesno.infra.smart.point.annotation.AgentPointAnnotation;
import com.alinesno.infra.smart.point.annotation.ScenePointAnnotation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
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
public class PointAspect {

    private final AipPointConsumer aipPointConsumer;

    @Value("${alinesno.point.enable:false}")
    private boolean pointEnable ;

    // 使用构造函数注入替代字段注入
    public PointAspect(AipPointConsumer aipPointConsumer) {
        this.aipPointConsumer = aipPointConsumer;
    }

    // 定义切入点：所有Controller类的方法
    @Pointcut("@within(org.springframework.stereotype.Controller) || " +
            "@within(org.springframework.web.bind.annotation.RestController)")
    public void controllerPointcut() {}

    @Before("controllerPointcut()")
    public void beforeControllerMethod(JoinPoint joinPoint) {
        if (!pointEnable) {
            return;
        }

        log.debug("aipPointConsumer = {}" , aipPointConsumer);

        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        AgentPointAnnotation agentPointAnnotation = AnnotationUtils.findAnnotation(method, AgentPointAnnotation.class);
        ScenePointAnnotation scenePointAnnotation = AnnotationUtils.findAnnotation(method, ScenePointAnnotation.class);

        if(agentPointAnnotation != null || scenePointAnnotation != null){

            // 验证用户登录状态
            if (!StpUtil.isLogin()) {
                throw new RpcServiceRuntimeException("用户未登录");
            }

            Long userId = Long.parseLong(StpUtil.getLoginId().toString());
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

            // 处理注解
            try {
                if(agentPointAnnotation != null){
                    new AgentPointProcessor(method).process(request, userId);
                }

                if(scenePointAnnotation != null){
                    new ScenePointProcessor(method).process(request, userId);
                }
            } catch (Exception e) {
                throw new RpcServiceRuntimeException(e);
            }
        }

    }
}