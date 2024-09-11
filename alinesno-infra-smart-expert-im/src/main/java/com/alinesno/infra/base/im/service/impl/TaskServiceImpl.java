package com.alinesno.infra.base.im.service.impl;

import com.alinesno.infra.base.im.dto.ChatMessageDto;
import com.alinesno.infra.base.im.dto.MessageTaskInfo;
import com.alinesno.infra.base.im.service.IMessageService;
import com.alinesno.infra.base.im.service.ISSEService;
import com.alinesno.infra.base.im.service.ITaskService;
import com.alinesno.infra.base.im.utils.AgentUtils;
import com.alinesno.infra.base.im.utils.TaskUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * TaskServiceImpl 主要用于处理任务，包括添加任务、获取任务等。
 */
@Slf4j
@Service
public class TaskServiceImpl implements ITaskService {

    @Autowired
    private IMessageService messageService ;

    @Autowired
    private ISSEService sseService;

    private static final int MAX_QUEUE_SIZE = 100;

    private final Queue<MessageTaskInfo> errorTaskQueue = new LinkedList<>();
    private final Queue<MessageTaskInfo> taskQueue = new LinkedList<>();
    private final ThreadPoolTaskExecutor executor;

    public TaskServiceImpl() {
        // 初始化线程池
        executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // 核心线程数
        executor.setMaxPoolSize(10); // 最大线程数
        executor.setQueueCapacity(MAX_QUEUE_SIZE); // 队列容量
        executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                log.error("Task queue is full, cannot add more tasks.");
            }
        });
        executor.initialize();
    }

    @Override
    public void addTask(MessageTaskInfo info) {
        synchronized (taskQueue) {
            if (taskQueue.size() < MAX_QUEUE_SIZE) { // 假设最大队列大小为100
                taskQueue.add(info);
                executor.execute(this::processTask);
            } else {
                log.warn("任务列表已满，不能再添加新任务.");
            }
        }
    }

    @Async
    private void processTask() {
        MessageTaskInfo taskInfo = taskQueue.poll(); // 获取并移除队首的任务
        if (taskInfo != null) {
            long startTime = System.currentTimeMillis() ;
            try {
                log.info("任务处理中: {}", taskInfo);

                Thread.sleep(5000);

                long endTime = System.currentTimeMillis() ;

                String usageTime = TaskUtils.formatTimeDifference(startTime, endTime);
                taskInfo.setUsageTime(usageTime);

                log.info("任务处理完成: {}", taskInfo);

                ChatMessageDto queMessage = AgentUtils.genTaskStatusMessageDto(taskInfo , usageTime) ;
                sseService.send(String.valueOf(taskInfo.getChannelId()), queMessage);

                // 保存到消息记录表中
                messageService.saveChatMessage(queMessage , taskInfo.getChannelId());

            } catch (Exception e) {
                log.error("发送消息通知前端失败:{}" , e.getMessage());
                errorTaskQueue.add(taskInfo);
                // throw new RuntimeException(e);
            } finally {
                // 在finally块中保证线程结束
                log.debug("任务处理完成.");
            }
        }
    }

    @Override
    public List<MessageTaskInfo> getTaskMessage(MessageTaskInfo taskInfo) {
        log.debug("查询任务信息.");
        // 这里应该返回与taskInfo相关的任务列表

        return null;
    }

    @Override
    public List<MessageTaskInfo> getTaskMessage(String channelId) {
        log.debug("查询任务信息.");

        // 创建一个列表来存储符合条件的任务信息
        List<MessageTaskInfo> filteredTasks = new ArrayList<>();

        // 使用迭代器遍历队列中的每个任务
        Iterator<MessageTaskInfo> iterator = errorTaskQueue.iterator();
        while (iterator.hasNext()) {
            MessageTaskInfo task = iterator.next();
            // 如果任务的 channelId 与给定的相匹配，则添加到列表中
            if (String.valueOf(task.getChannelId()).equals(channelId)) {
                filteredTasks.add(task);
                // 移除匹配的任务
                iterator.remove();
            }
        }

        return filteredTasks; // 返回匹配的任务列表
    }

}
