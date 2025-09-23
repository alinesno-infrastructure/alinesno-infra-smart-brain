package com.alinesno.infra.smart.assistant.point.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 积分消费事件
 * 用于在积分消费时发布事件，实现业务解耦
 */
@Getter
public class ScenePointConsumeEvent extends ApplicationEvent {

    // 用户ID
    private final long userId;

    // 组织ID
    private final long orgId;

    // 角色ID
    private final long sceneId;

    // 任务ID
    private final long taskId;

    public ScenePointConsumeEvent(long userId, long orgId, long taskId , long sceneId) {
        super(userId);
        this.userId = userId;
        this.orgId = orgId;
        this.sceneId = sceneId;
        this.taskId = taskId;
    }

}
    