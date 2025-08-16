//package com.alinesno.infra.smart.assistant.point;
//
//import cn.dev33.satoken.stp.StpUtil;
//import com.alinesno.infra.common.web.adapter.login.account.CurrentAccountJwt;
//import com.alinesno.infra.smart.assistant.adapter.AipPointConsumer;
//import com.alinesno.infra.smart.assistant.point.processor.AgentPointProcessor;
//import com.alinesno.infra.smart.assistant.point.processor.ScenePointProcessor;
//import com.alinesno.infra.smart.point.annotation.AgentPointAnnotation;
//import com.alinesno.infra.smart.point.annotation.ScenePointAnnotation;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.annotation.AnnotationUtils;
//import org.springframework.stereotype.Component;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import javax.lang.exception.RpcServiceRuntimeException;
//import java.lang.reflect.Method;
//
//@Aspect
//@Component
//@Slf4j
//public class PointSceneAspect {
//
//    private final AipPointConsumer aipPointConsumer;
//
//    @Value("${alinesno.point.enable:false}")
//    private boolean pointEnable ;
//
//    // 使用构造函数注入替代字段注入
//    public PointSceneAspect(AipPointConsumer aipPointConsumer) {
//        this.aipPointConsumer = aipPointConsumer;
//    }
//
//    // 定义切入点：同时包含两个注解的方法
//    @Pointcut("@annotation(com.alinesno.infra.smart.point.annotation.ScenePointAnnotation)")
//    public void pointAnnotationsPointcut() {}
//
//    @Around("pointAnnotationsPointcut()")
//    public Object aroundControllerMethod(ProceedingJoinPoint joinPoint) throws Throwable {
//        if (!pointEnable) {
//            return joinPoint.proceed(); // 直接放行
//        }
//
//        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
//        long startTime = System.currentTimeMillis();
//
//        // 1. 原有鉴权逻辑
//        ScenePointAnnotation scenePointAnnotation = AnnotationUtils.findAnnotation(method, ScenePointAnnotation.class);
//
//        if(scenePointAnnotation != null){
//            // 验证登录状态
//            if (!StpUtil.isLogin()) {
//                throw new RpcServiceRuntimeException("用户未登录");
//            }
//
//            Long orgId = CurrentAccountJwt.get().getOrgId();
//            Long userId = CurrentAccountJwt.get().getId();
//            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
//
//            // 初始化处理器
//            ScenePointProcessor sceneProcessor = new ScenePointProcessor(method , scenePointAnnotation) ;
//
//            // 检查限流
//            if (!sceneProcessor.checkRateLimit(userId, orgId)) {
//                throw new RpcServiceRuntimeException("请求频率超过限制");
//            }
//
//            // 2. 执行目标方法并统计耗时
//            try {
//                Object result = joinPoint.proceed(); // 执行实际方法
//                long cost = System.currentTimeMillis() - startTime;
//                log.info("方法 {} 执行成功，耗时 {} ms", method.getName(), cost);
//
//                // 处理积分消耗（只在成功时）
//                sceneProcessor.deductPoints(userId, orgId);
//
//                return result;
//            } catch (Exception e) {
//                long cost = System.currentTimeMillis() - startTime;
//                log.error("方法 {} 执行失败，耗时 {} ms，异常: {}", method.getName(), cost, e.getMessage());
//                throw e;
//            } finally {
//                // 3. 原有注解处理逻辑
//                try {
//                    sceneProcessor.process(request, userId, orgId);
//                } catch (Exception e) {
//                    log.error("积分处理异常", e);
//                }
//            }
//        }
//
//        return joinPoint.proceed();
//    }
//}