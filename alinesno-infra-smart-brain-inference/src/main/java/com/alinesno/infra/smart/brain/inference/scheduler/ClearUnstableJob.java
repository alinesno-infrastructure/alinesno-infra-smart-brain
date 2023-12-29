package com.alinesno.infra.smart.brain.inference.scheduler;

import com.alinesno.infra.smart.brain.entity.GenerateTaskEntity;
import com.alinesno.infra.smart.brain.enums.TaskStatus;
import com.alinesno.infra.smart.brain.service.IGenerateTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 清理不稳定的任务(即状态为进行中，但是结果未结束的任务清理，这里统一将超时结果未结束的任务状态设置为失败)
 */
@Slf4j
@Component
public class ClearUnstableJob {

    private static final int JOB_OUT_TIME = 2 * 60 ; // 默认任务超过2分钟则为超时

    @Autowired
    private IGenerateTaskService generateTaskService ;

    @Scheduled(initialDelay = 30000 , fixedDelay = 60000)
    private void loadTasks() {

        List<GenerateTaskEntity> tasks = generateTaskService.getAllUnstableTasks(JOB_OUT_TIME);

        for (GenerateTaskEntity task : tasks) {  // 设置为失败的任务
            task.setTaskStatus(TaskStatus.FAILED.getValue());
            generateTaskService.update(task);
        }
    }

}
