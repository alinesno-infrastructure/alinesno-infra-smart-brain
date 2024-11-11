package com.alinesno.infra.smart.assistant.screen.event;

import org.springframework.context.ApplicationEvent;

/**
 * 任务分配事件
 */
public class TaskAssignedEvent extends ApplicationEvent {
    private final String task;

    public TaskAssignedEvent(Object source, String task) {
        super(source);
        this.task = task;
    }

    public String getTask() {
        return task;
    }
}