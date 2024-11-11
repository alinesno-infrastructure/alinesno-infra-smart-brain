package com.alinesno.infra.smart.assistant.screen.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class LeaderTaskCompletionListener {

    private final ApplicationEventPublisher publisher;

    public LeaderTaskCompletionListener(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @EventListener
    public void handleTaskCompletion(TaskCompletionEvent event) {
        if (event.isCompleted()) {
            System.out.println("Leader confirmed that task: " + event.getTaskId() + " has been completed.");
        } else {
            System.out.println("Leader found that task: " + event.getTaskId() + " was not completed.");
            // 如果任务未完成，重新分配任务
            publisher.publishEvent(new TaskAssignedEvent(this, event.getTaskId()));
        }
    }
}