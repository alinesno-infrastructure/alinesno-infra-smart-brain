package com.alinesno.infra.smart.assistant.screen.event;

import com.alinesno.infra.smart.assistant.screen.dto.RoleTaskDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 任务分配事件
 */
@Getter
public class TaskAssignedEvent extends ApplicationEvent {

    private final RoleTaskDto task;

    public TaskAssignedEvent(Object source, RoleTaskDto task) {
        super(source);
        this.task = task;
    }

}