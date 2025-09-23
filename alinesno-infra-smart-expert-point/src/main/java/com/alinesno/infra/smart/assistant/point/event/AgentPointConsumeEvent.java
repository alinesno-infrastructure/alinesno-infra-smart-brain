package com.alinesno.infra.smart.assistant.point.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 积分消费事件
 * 用于在积分消费时发布事件，实现业务解耦
 */
@Getter
public class AgentPointConsumeEvent extends ApplicationEvent {

    // 用户ID
    private final long userId;

    // 组织ID
    private final long orgId;

    // 角色ID
    private final long roleId;

    /**
     * 构造积分消费事件
     *
     * @param userId    用户ID
     * @param orgId     组织ID
     */
    public AgentPointConsumeEvent(long userId, long orgId, long roleId) {
        super(userId);
        this.userId = userId;
        this.orgId = orgId;
        this.roleId = roleId;
    }
}
    