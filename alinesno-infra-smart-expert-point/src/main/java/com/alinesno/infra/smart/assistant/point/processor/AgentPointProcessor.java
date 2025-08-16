package com.alinesno.infra.smart.assistant.point.processor;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.core.cache.RedisUtils;
import com.alinesno.infra.smart.point.annotation.AgentPointAnnotation;
import com.alinesno.infra.smart.point.enums.ParamSource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.redis.core.RedisTemplate;

import javax.lang.exception.RpcServiceRuntimeException;
import java.lang.reflect.Method;
import java.util.stream.Collectors;

@Slf4j
public class AgentPointProcessor extends PointProcessorParent {

    private final AgentPointAnnotation annotation;
    private final Method method;
    private double currentRateLimit; // 当前限流值
    private int pointsToDeduct;     // 需要扣除的积分

    public AgentPointProcessor(Method method , AgentPointAnnotation annotation) {
        this.method = method;
        this.currentRateLimit = 1 ; // 可从配置获取
        this.pointsToDeduct = 1; // 默认扣除1积分，可从配置获取
        this.annotation = annotation;
    }


    public boolean checkRateLimit(Long userId, Long orgId) {
        return super.checkRateLimit(orgId, userId , currentRateLimit);
    }

    public void deductPoints(Long userId, Long orgId) {
        super.recordPointDeduction(orgId, userId, pointsToDeduct);
    }

    @Override
    public void process(HttpServletRequest request, Long userId, Long orgId) {
        // 保持空实现，由aspect直接调用checkRateLimit和deductPoints

        String key = RATE_LIMIT_PREFIX + "org:" + orgId;

        // 删除前建议先检查是否存在
        if (RedisUtils.isExistsObject(key)) {
            RedisUtils.deleteObject(key);
        }
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