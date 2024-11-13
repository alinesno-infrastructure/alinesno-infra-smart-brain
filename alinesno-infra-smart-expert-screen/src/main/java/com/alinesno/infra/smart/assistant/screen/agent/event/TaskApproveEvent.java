package com.alinesno.infra.smart.assistant.screen.agent.event;

import com.alinesno.infra.smart.assistant.screen.dto.RoleTaskDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 任务完成事件
 */
@Getter
public class TaskApproveEvent extends ApplicationEvent {

    private final RoleTaskDto task;

    public TaskApproveEvent(Object source, RoleTaskDto task) {
        super(source);
        this.task = task;
    }

}
