package com.alinesno.infra.smart.assistant.screen.agent.event;

import com.alinesno.infra.smart.assistant.screen.dto.RoleTaskDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 任务完成事件
 */
@Getter
public class TaskCompletionEvent extends ApplicationEvent {

    private final RoleTaskDto task;
    private final boolean isCompleted;

    public TaskCompletionEvent(Object source, RoleTaskDto task, boolean isCompleted) {
        super(source);
        this.task = task;
        this.isCompleted = isCompleted;
    }

    public boolean isCompleted() {
        return isCompleted;
    }
}
