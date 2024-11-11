package com.alinesno.infra.smart.assistant.screen.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class WorkerTaskListener {

    private final ApplicationEventPublisher publisher;

    public WorkerTaskListener(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @EventListener
    public void handleTaskAssigned(TaskAssignedEvent event) {
        System.out.println("Worker received task: " + event.getTask());
        // 模拟任务处理
        try {
            Thread.sleep(1000); // 假设任务需要1秒来完成
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 发送任务完成事件
        boolean isCompleted = true; // 假设任务总是成功完成
        publisher.publishEvent(new TaskCompletionEvent(this, event.getTask(), isCompleted));
    }
}