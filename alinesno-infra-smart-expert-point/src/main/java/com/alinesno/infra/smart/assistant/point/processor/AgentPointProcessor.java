package com.alinesno.infra.smart.assistant.point.processor;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.smart.point.annotation.AgentPointAnnotation;
import com.alinesno.infra.smart.point.enums.ParamSource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;

import javax.lang.exception.RpcServiceRuntimeException;
import java.lang.reflect.Method;
import java.util.stream.Collectors;

@Slf4j
public class AgentPointProcessor implements PointAnnotationProcessor {

    private final AgentPointAnnotation annotation;
    private final Method method;

    public AgentPointProcessor(Method method) {
        this.method = method;
        this.annotation = AnnotationUtils.findAnnotation(method, AgentPointAnnotation.class);
    }

    @Override
    public void process(HttpServletRequest request, Long userId) throws Exception {
        if (annotation == null) {
            return;
        }

        String roleId = getRoleIdFromRequest(request);
        log.debug("处理AgentPoint注解 - userId: {}, roleId: {}", userId, roleId);

        // 这里可以继续处理AgentPoint相关的积分逻辑
    }

    private String getRoleIdFromRequest(HttpServletRequest request) {
        String paramName = annotation.paramName();

        if (annotation.paramType() == ParamSource.PARAMS) {
            return request.getParameter(paramName);
        } else {
            try {
                String body = request.getReader().lines().collect(Collectors.joining());
                JSONObject jsonObject = JSONObject.parseObject(body);
                return jsonObject.getString(paramName);
            } catch (Exception e) {
                log.error("从请求体获取roleId失败", e);
                throw new RpcServiceRuntimeException("获取roleId参数失败");
            }
        }
    }
}