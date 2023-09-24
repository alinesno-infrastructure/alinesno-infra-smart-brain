package com.alinesno.infra.smart.brain.scheduler;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.smart.brain.entity.GenerateTaskEntity;
import com.alinesno.infra.smart.brain.enums.TaskStatus;
import com.alinesno.infra.smart.brain.service.IGenerateTaskService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class LoadTaskProcess {

    @Autowired
    private IGenerateTaskService generateTaskService ;

    @Autowired
    private TaskProcessor taskProcessor ;

    @PostConstruct
    private void loadTasks() {

        taskProcessor.init();

        List<GenerateTaskEntity> tasks = generateTaskService.getAllUnfinishedTasks();
        for (GenerateTaskEntity task : tasks) {

            log.debug("task = {}" , JSONObject.toJSON(task));

            task.setTaskStatus(TaskStatus.RUNNING.getValue());
            generateTaskService.update(task);

            taskProcessor.addTaskToDisruptor(task);
        }
    }

}
