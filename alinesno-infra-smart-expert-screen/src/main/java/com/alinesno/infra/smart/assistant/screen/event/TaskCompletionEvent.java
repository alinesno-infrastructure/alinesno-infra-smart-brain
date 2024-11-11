package com.alinesno.infra.smart.assistant.screen.event;

import org.springframework.context.ApplicationEvent;

/**
 * 任务完成事件
 */
public class TaskCompletionEvent extends ApplicationEvent {
    private final String taskId;
    private final boolean isCompleted;

    public TaskCompletionEvent(Object source, String taskId, boolean isCompleted) {
        super(source);
        this.taskId = taskId;
        this.isCompleted = isCompleted;
    }

    public String getTaskId() {
        return taskId;
    }

    public boolean isCompleted() {
        return isCompleted;
    }
}
