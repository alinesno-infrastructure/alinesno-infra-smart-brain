//package com.alinesno.infra.smart.assistant.scene.scene.productResearch.job;
//
//import com.alinesno.infra.smart.assistant.scene.scene.productResearch.service.IProjectManagerService;
//import com.alinesno.infra.smart.scene.entity.ProjectManagerEntity;
//import com.alinesno.infra.smart.scene.enums.ProjectStatus;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//// 全局定时任务扫描（用于处理服务重启后恢复定时任务）
//@Slf4j
//@Component
//public class IncrementalCollectScheduler {
//
//    @Autowired
//    private IProjectManagerService projectManagerService;
//
//    // 每天凌晨1点扫描所有已完成项目，恢复定时任务（可根据需求调整频率）
//    @Scheduled(cron = "0 0 1 * * ?")
//    public void restoreScheduledTasks() {
//        log.info("开始恢复定时增量采集任务");
//        List<ProjectManagerEntity> projects = projectManagerService.listByStatus(ProjectStatus.COMPLETED.name());
//
//        for (ProjectManagerEntity project : projects) {
//            if (project.getSyncInterval() > 0) {
//                projectManagerService.scheduleNextIncrementalCollect(project);
//                log.info("恢复项目{}的定时增量采集任务", project.getId());
//            }
//        }
//    }
//}