package com.alinesno.infra.smart.assistant.point.processor;

import com.alinesno.infra.common.core.context.SpringContext;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class PointProcessorParent implements PointAnnotationProcessor {

    @SuppressWarnings("unchecked")
    private final RedisTemplate<String, Object> redisTemplate = (RedisTemplate<String, Object>) SpringContext.getBean("redisTemplate");;
    public static final String RATE_LIMIT_PREFIX = "point_rate_limit:";

    /**
     * 检查限流
     * @param orgId 组织ID
     * @param userId 用户ID
     * @param currentRateLimit 当前限流阈值
     * @return true 允许访问，false 触发限流
     */
    public boolean checkRateLimit(Long orgId, Long userId, double currentRateLimit) {
        String key = RATE_LIMIT_PREFIX + "org:" + orgId;
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();

        try {
            Object result = ops.get(key);
            if (result == null) {
                return true; // 无记录，默认允许
            }

            double currentCount;
            if (result instanceof Number) {
                currentCount = ((Number) result).doubleValue();
            } else {
                currentCount = Double.parseDouble(result.toString());
            }

            return currentCount <= currentRateLimit;
        } catch (Exception e) {
            log.error("限流检查异常，key={}", key, e);
            return true; // 异常时默认允许
        }
    }

    /**
     * 设置限流值
     * @param orgId 组织ID
     * @param userId 用户ID
     */
    public void setRateLimit(Long orgId, Long userId) {
        String key = RATE_LIMIT_PREFIX + "org:" + orgId;
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();

        try {
            ops.increment(key, 1); // 原子性+1
            redisTemplate.expire(key, 1, TimeUnit.HOURS); // 设置1小时过期
        } catch (Exception e) {
            log.error("设置限流值异常，key={}", key, e);
            ops.set(key, 1, 1, TimeUnit.HOURS); // 异常时初始化
        }
    }

    /**
     * 记录积分扣除
     */
    protected void recordPointDeduction(Long orgId, Long userId, int points) {
        // 积分扣除逻辑...
    }
}