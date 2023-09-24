package com.alinesno.infra.smart.brain.event;

import com.alinesno.infra.smart.brain.entity.GenerateTaskEntity;
import com.alinesno.infra.smart.brain.enums.TaskStatus;
import com.alinesno.infra.smart.brain.service.IGenerateTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ChatEventListener {

    @Autowired
    private IGenerateTaskService taskService ;

    @EventListener
    public void handleEvent(GenerateTaskEntity event) {
        // 处理事件逻辑
        event.setTaskStatus(TaskStatus.COMPLETED.getValue());
        taskService.update(event);
    }
}
