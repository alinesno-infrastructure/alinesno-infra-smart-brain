package com.alinesno.infra.smart.assistant.monitor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 控制台表格化线程监控控制器
 * 返回适合CMD显示的表格格式数据
 */
@RestController
public class ChatMonitorController {

    // 简单的测试接口
    @GetMapping("/helloWorld")
    public String helloWorld() {
        return "你好，世界! 当前时间戳: " + System.currentTimeMillis();
    }

    @Autowired
    private ThreadPoolTaskExecutor chatThreadPool;

    /**
     * 获取线程池状态信息
     */
    @GetMapping("/chatThreadPoolStatus")
    public String getThreadPoolStatus() {
        ThreadPoolExecutor executor = chatThreadPool.getThreadPoolExecutor();

        return "+--------------------------------+------------+\n" +
                "|       线程池状态监控           |    数值    |\n" +
                "+--------------------------------+------------+\n" +
                String.format("| %-30s | %10d |\n", "核心线程数", chatThreadPool.getCorePoolSize()) +
                String.format("| %-30s | %10d |\n", "最大线程数", chatThreadPool.getMaxPoolSize()) +
                String.format("| %-30s | %10d |\n", "当前活跃线程数", executor.getActiveCount()) +
                String.format("| %-30s | %10d |\n", "当前池中线程数", executor.getPoolSize()) +
                String.format("| %-30s | %10d |\n", "已完成任务数", executor.getCompletedTaskCount()) +
                String.format("| %-30s | %10d |\n", "队列中等待任务数", executor.getQueue().size()) +
                String.format("| %-30s | %10d |\n", "总任务数(执行+完成+排队)",  executor.getTaskCount()) +
                String.format("| %-30s | %10d |\n", "剩余队列容量", executor.getQueue().remainingCapacity()) +
                "+--------------------------------+------------+\n";
    }

}