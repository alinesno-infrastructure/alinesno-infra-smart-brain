package com.alinesno.infra.smart.brain.event;

import com.alinesno.infra.smart.brain.entity.GenerateTaskEntity;
import com.alinesno.infra.smart.brain.enums.TaskStatus;
import com.alinesno.infra.smart.brain.service.IGenerateTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ChatEventListener implements ApplicationListener<TaskEvent> {

    @Autowired
    private IGenerateTaskService taskService ;

    @Override
    public void onApplicationEvent(TaskEvent event) {
        GenerateTaskEntity entity = taskService.getById(event.getId()) ;

        // 处理事件逻辑
        entity.setTaskStatus(TaskStatus.COMPLETED.getValue());
        entity.setAssistantContent(event.getAssistantContent());

        taskService.update(entity);
    }
}
