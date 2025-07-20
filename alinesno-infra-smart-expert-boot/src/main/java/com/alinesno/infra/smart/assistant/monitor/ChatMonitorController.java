package com.alinesno.infra.smart.assistant.monitor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 控制台表格化监控控制器
 * 返回适合CMD显示的表格格式数据（包含线程池和数据库连接池）
 */
@Slf4j
@RestController
public class ChatMonitorController {

    @Autowired(required = false)
    private DataSource dataSource;

    @Autowired
    private ThreadPoolTaskExecutor chatThreadPool;

    @Autowired
    private ServletWebServerApplicationContext serverContext;

    /**
     * 获取综合监控信息（线程池+数据库连接池）
     */
    @GetMapping("/chatThreadPoolStatus")
    public String getSystemStatus() {
        StringBuilder sb = new StringBuilder();

        // 线程池监控信息
        ThreadPoolExecutor executor = chatThreadPool.getThreadPoolExecutor();
        sb.append("+--------------------------------+------------+\n")
                .append("|       线程池状态监控           |    数值    |\n")
                .append("+--------------------------------+------------+\n")
                .append(String.format("| %-30s | %10d |\n", "核心线程数", chatThreadPool.getCorePoolSize()))
                .append(String.format("| %-30s | %10d |\n", "最大线程数", chatThreadPool.getMaxPoolSize()))
                .append(String.format("| %-30s | %10d |\n", "当前活跃线程数", executor.getActiveCount()))
                .append(String.format("| %-30s | %10d |\n", "当前池中线程数", executor.getPoolSize()))
                .append(String.format("| %-30s | %10d |\n", "已完成任务数", executor.getCompletedTaskCount()))
                .append(String.format("| %-30s | %10d |\n", "队列中等待任务数", executor.getQueue().size()))
                .append(String.format("| %-30s | %10d |\n", "总任务数(执行+完成+排队)", executor.getTaskCount()))
                .append(String.format("| %-30s | %10d |\n", "剩余队列容量", executor.getQueue().remainingCapacity()))
                .append("+--------------------------------+------------+\n\n");

        // 通用数据源监控信息
        if (dataSource != null) {
            try (Connection connection = dataSource.getConnection()) {
                sb.append("+------------------------------------+------------+\n")
                        .append("|      数据库连接池状态监控         |    数值    |\n")
                        .append("+------------------------------------+------------+\n");

                if (dataSource != null) {
                    // 尝试获取HikariCP数据源信息
                    try {
                        Object pool = dataSource.getClass().getMethod("getHikariPoolMXBean").invoke(dataSource);
                        sb.append(String.format("| %-34s | %10d |\n", "活动连接数", pool.getClass().getMethod("getActiveConnections").invoke(pool)))
                                .append(String.format("| %-34s | %10d |\n", "空闲连接数", pool.getClass().getMethod("getIdleConnections").invoke(pool)))
                                .append(String.format("| %-34s | %10d |\n", "等待获取连接线程数", pool.getClass().getMethod("getThreadsAwaitingConnection").invoke(pool)))
                                .append(String.format("| %-34s | %10d |\n", "总连接数", pool.getClass().getMethod("getTotalConnections").invoke(pool)));
                    } catch (Exception e) {
                        // 如果不是HikariCP，使用通用JDBC信息
                        sb.append(String.format("| %-34s | %10s |\n", "连接状态", "已连接"))
                                .append(String.format("| %-34s | %10s |\n", "连接有效性", connection.isValid(1) ? "有效" : "无效"));
                    }
                }
                sb.append("+------------------------------------+------------+\n");
            } catch (SQLException e) {
                sb.append("\n数据库连接池监控信息获取失败: ").append(e.getMessage()).append("\n");
            }
        } else {
            sb.append("\n数据源未配置\n");
        }

        return sb.toString();
    }

    // 简单的测试接口
    @GetMapping("/helloWorld")
    public String helloWorld() {
        return "你好，世界! 当前时间戳: " + System.currentTimeMillis();
    }
}