package com.alinesno.infra.base.search.memory.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 记忆反思任务
 */
@Slf4j
@Service
public class MemoryReflectionJob {

    /**
     * 执行任务（每1个小时做一次反思)
     */
    @Scheduled(cron = "0 0 */1 * * ?")
    public void execute() {
        // TODO: 执行记忆反思任务
        log.debug("执行记忆反思任务");

        // TODO：记忆执行更新任务
        log.debug("记忆执行更新任务");
    }

}
