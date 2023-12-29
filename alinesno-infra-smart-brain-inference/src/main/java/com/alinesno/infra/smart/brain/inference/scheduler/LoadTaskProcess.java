package com.alinesno.infra.smart.brain.inference.scheduler;

import com.alinesno.infra.smart.brain.entity.GenerateTaskEntity;
import com.alinesno.infra.smart.brain.enums.TaskStatus;
import com.alinesno.infra.smart.brain.service.IFileProcessingService;
import com.alinesno.infra.smart.brain.service.IGenerateTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class LoadTaskProcess {

    @Autowired
    private IGenerateTaskService generateTaskService ;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor ;

    @Autowired
    private IFileProcessingService fileProcessingService ;

    @Value("${alinesno.infra.smart.brain.max-retry:5}")
    private int maxRetryCount ;

    @Scheduled(initialDelay = 10000 , fixedDelay = 60000)
    private void loadTasks() {

        List<GenerateTaskEntity> tasks = generateTaskService.getAllUnfinishedTasks(maxRetryCount);

        for (GenerateTaskEntity task : tasks) {

            task.setTaskStatus(TaskStatus.RUNNING.getValue());
            task.setUpdateTime(new Date());

            generateTaskService.update(task);

            fileProcessingService.processFile(task);
        }
    }

}
