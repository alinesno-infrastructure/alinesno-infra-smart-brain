package com.alinesno.infra.smart.assistant.point;

import cn.dev33.satoken.stp.StpUtil;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.common.core.cache.RedisUtils;
import com.alinesno.infra.smart.assistant.adapter.AipPointConsumer;
import com.alinesno.infra.smart.assistant.adapter.dto.PointSettingDto;
import com.alinesno.infra.smart.assistant.adapter.dto.UserPointDto;
import com.alinesno.infra.smart.assistant.point.config.PointProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.lang.exception.RpcServiceRuntimeException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 积分系统拦截器
 *
 * <p>实现基于请求URI的积分检查与扣减逻辑，主要功能包括：
 * <ol>
 *   <li>检查请求是否需要积分验证</li>
 *   <li>验证用户登录状态</li>
 *   <li>检查用户当前积分是否充足</li>
 *   <li>执行积分扣减操作</li>
 * </ol>
 *
 * <p>积分规则通过Redis集群共享，支持多节点部署环境
 *
 * @author luoxiaodong
 * @version 1.0.0
 * @since 2023-09-20
 */
@Slf4j
@Component
public class PointInterceptor implements HandlerInterceptor {

    /**
     * Redis中存储积分规则的key
     */
    private static final String POINT_RULES_KEY = "smart:assistant:point:rules";

    /**
     * Redis中存储最后加载时间的key
     */
    private static final String RULE_LOAD_TIME_KEY = "smart:assistant:point:lastLoadTime";

    private final AipPointConsumer aipPointConsumer;
    private final PointProperties pointProperties;

    public PointInterceptor(AipPointConsumer aipPointConsumer, PointProperties pointProperties) {
        this.aipPointConsumer = aipPointConsumer;
        this.pointProperties = pointProperties;
    }

    /**
     * 预处理拦截方法
     *
     * @param request  HTTP请求对象
     * @param response HTTP响应对象
     * @param handler  处理器对象
     * @return 是否继续执行后续流程
     * @throws Exception 处理过程中的异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        if (!pointProperties.isEnabled()) {
            return true;
        }

        String requestUrl = request.getRequestURI();

        // 1. 获取所需积分（从Redis）
        int requiredPoints = getRequiredPoints(requestUrl);
        if (requiredPoints <= 0) {
            return true;
        }

        // 2. 检查登录状态
        if (!StpUtil.isLogin()) {
            log.warn("Unauthorized access attempt: {}", requestUrl);
            throw new RpcServiceRuntimeException("请先登录系统");
        }

        Long userId = Long.parseLong(StpUtil.getLoginId().toString());

        try {
            // 3. 检查用户积分
            R<UserPointDto> pointResult = aipPointConsumer.getUserPoint(userId);
            if (R.isError(pointResult)) {
                log.error("Failed to get user points - userId: {}, error: {}",  userId, pointResult.getMsg());
                throw new RpcServiceRuntimeException("积分服务暂不可用");
            }

            int currentPoints = Optional.ofNullable(pointResult.getData())
                    .map(UserPointDto::getPoints)
                    .orElse(0);

            if (currentPoints < requiredPoints) {
                log.warn("Insufficient points - userId: {}, required: {}, current: {}",  userId, requiredPoints, currentPoints);
                throw new RpcServiceRuntimeException(String.format("积分不足，需要%d积分，当前剩余%d积分", requiredPoints, currentPoints));
            }

            // 4. 扣减积分
            R<Boolean> deductResult = aipPointConsumer.reducePoint(userId, requiredPoints);
            if (R.isError(deductResult)) {
                log.error("Failed to deduct points - userId: {}, points: {}, error: {}", userId, requiredPoints, deductResult.getMsg());
                throw new RpcServiceRuntimeException("积分扣减失败");
            }

            log.info("Points deducted - userId: {}, points: {}, uri: {}", userId, requiredPoints, requestUrl);
            return true;

        } catch (RpcServiceRuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Point system error - uri: {}", requestUrl, e);
            throw new RpcServiceRuntimeException("系统繁忙，请稍后重试");
        }
    }

    private int getRequiredPoints(String requestUrl) {
        // 检查缓存是否需要刷新
        if (isCacheExpired()) {
            refreshPointRules();
        }

        // 从Redis获取积分规则
        Map<String, Integer> rules = RedisUtils.getCacheMap(POINT_RULES_KEY);
        return rules.getOrDefault(requestUrl, 0);
    }

    private boolean isCacheExpired() {
        long lastLoadTime = RedisUtils.getAtomicValue(RULE_LOAD_TIME_KEY);
        return System.currentTimeMillis() - lastLoadTime >
                TimeUnit.SECONDS.toMillis(pointProperties.getCacheDuration());
    }

    private synchronized void refreshPointRules() {
        // 双重检查锁
        if (!isCacheExpired()) {
            return;
        }

        try {
            R<List<PointSettingDto>> result = aipPointConsumer.getAllPointSetting();
            if (R.isSuccess(result) && result.getData() != null) {
                Map<String, Integer> newRules = new ConcurrentHashMap<>();
                result.getData().forEach(dto ->
                        newRules.put(dto.getUri(), dto.getPointValue()));

                // 更新Redis缓存
                RedisUtils.setCacheMap(POINT_RULES_KEY, newRules);
                RedisUtils.setAtomicValue(RULE_LOAD_TIME_KEY, System.currentTimeMillis());

                log.info("Point rules refreshed, loaded {} rules", newRules.size());
            }
        } catch (Exception e) {
            log.error("Failed to refresh point rules", e);
        }
    }
}