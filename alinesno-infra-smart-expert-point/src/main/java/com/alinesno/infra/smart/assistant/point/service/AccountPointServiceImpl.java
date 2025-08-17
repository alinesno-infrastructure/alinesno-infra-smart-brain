package com.alinesno.infra.smart.assistant.point.service;

import com.alinesno.infra.common.core.cache.RedisUtils;
import com.alinesno.infra.smart.point.constants.PointConstants;
import com.alinesno.infra.smart.point.service.IAccountPointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * TODO 积分限流与清零的逻辑错误
 * 账户积分服务实现类
 */
@Slf4j
@Service
public class AccountPointServiceImpl implements IAccountPointService {

    @Value("${alinesno.point.enable:false}")
    private boolean pointEnable ;

    // 会话计数器的TTL(在时间范围内容最多的会话次数)
    private static final Duration SESSION_TTL = Duration.ofMinutes(1);

    // 场景任务计数器TTL(在时间范围内的最多执行次数)
    private static final Duration TASK_TTL = Duration.ofHours(1);

    @Override
    public boolean isOpenPoint() {
        return pointEnable ;
    }

    @Override
    public void startSession(long userId, long orgId , long roleId) {
        String key = String.format(PointConstants.ORG_COUNTER_KEY, orgId);
        long count = RedisUtils.incrAtomicValue(key);
        RedisUtils.expire(key, SESSION_TTL);

        log.debug("用户[{}]启动会话 | Org[{}] 计数+1 → 当前: {}", userId, orgId, count);
    }

    @Override
    public void endSession(long userId, long orgId , long roleId) {
        String key = String.format(PointConstants.ORG_COUNTER_KEY, orgId);
        long count = RedisUtils.decrAtomicValue(key);

        // 防御负数
        if (count < 0) {
            RedisUtils.setAtomicValue(key, 0);
            count = 0;
        }
        RedisUtils.expire(key, SESSION_TTL);

        log.debug("用户[{}]结束会话 | Org[{}] 计数-1 → 剩余: {}", userId, orgId, count);

        // TODO 扣除用户积分
    }

    /**
     * 获取指定组织的活跃会话数（新增方法）
     */
    public long getOrgSessionCount(long orgId) {
        String key = String.format(PointConstants.ORG_COUNTER_KEY, orgId);
        return Math.max(RedisUtils.getAtomicValue(key), 0);
    }

    // 新增场景任务统计方法
    @Override
    public void startSceneTask(long userId, long orgId, long roleId, long taskId) {
        // 组织级任务计数
        String orgTaskKey = String.format(PointConstants.ORG_TASK_COUNTER_KEY, orgId);
        long orgTaskCount = RedisUtils.incrAtomicValue(orgTaskKey);
        RedisUtils.expire(orgTaskKey, TASK_TTL);

        log.info("场景任务启动 | 用户[{}] | 任务[{}] | Org[{}] 任务数+1 → {}",  userId, taskId, orgId, orgTaskCount);
    }

    @Override
    public void endSceneTask(long userId, long orgId, long roleId, long taskId) {
        // 组织级任务计数
        String orgTaskKey = String.format(PointConstants.ORG_TASK_COUNTER_KEY, orgId);
        long orgTaskCount = RedisUtils.decrAtomicValue(orgTaskKey);

        // 防御负数
        if (orgTaskCount < 0) {
            RedisUtils.setAtomicValue(orgTaskKey, 0);
            orgTaskCount = 0;
        }
        RedisUtils.expire(orgTaskKey, TASK_TTL);

        log.info("场景任务结束 | 用户[{}] | 任务[{}] | Org[{}] 任务数-1 → {}",  userId, taskId, orgId, orgTaskCount);
    }

    @Override
    public long getOrgSceneTaskCount(long orgId) {
        String orgTaskKey = String.format(PointConstants.ORG_TASK_COUNTER_KEY, orgId);
        return Math.max(RedisUtils.getAtomicValue(orgTaskKey), 0);
    }

}
