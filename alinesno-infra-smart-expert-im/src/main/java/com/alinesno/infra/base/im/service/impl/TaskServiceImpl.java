package com.alinesno.infra.base.im.service.impl;

import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.dto.ChatMessageDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.enums.TaskStatusEnums;
import com.alinesno.infra.smart.im.service.IMessageService;
import com.alinesno.infra.smart.im.service.ISSEService;
import com.alinesno.infra.smart.im.service.ITaskService;
import com.alinesno.infra.smart.utils.AgentUtils;
import com.alinesno.infra.smart.utils.FilterWordUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
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
    private IIndustryRoleService roleService ;

    @Autowired
    private ISSEService sseService;

    @Autowired
    private ThreadPoolTaskExecutor chatThreadPool;

    @Override
    public void addTask(MessageTaskInfo info) {
        // 使用线程池异步执行任务
        chatThreadPool.execute(() -> processTask(info));
    }

    private void processTask(MessageTaskInfo taskInfo) {

        if (taskInfo != null) {
            try {
                log.info("任务处理中: {}", taskInfo);

                CompletableFuture<WorkflowExecutionDto> genContent  = roleService.runRoleAgent(taskInfo) ;
                // 处理消息结果
                genContent.whenComplete((result, ex) -> {
                    try {
                        handleWorkflowMessage(taskInfo, result) ;
                    } catch (Exception e) {
                        log.error("Failed to handle workflow message", e);
                    }
                });

            } catch (Exception e) {
                log.error("发送消息通知前端失败:", e);
            } finally {
                // 在finally块中保证线程结束
                log.debug("任务处理完成.");
            }
        }
    }

    @SneakyThrows
    @Override
    public void handleWorkflowMessage(MessageTaskInfo taskInfo, WorkflowExecutionDto genContent) {

        String channelId = String.valueOf(taskInfo.getChannelId()) ;
        String chatStreamId = taskInfo.getChannelStreamId() ;

        // 如果是过滤结果词，则不做输出
        if(FilterWordUtils.contains(genContent.getGenContent())){
            sseService.sendDone(chatStreamId);
            return ;
        }

        taskInfo.setUsageTime(genContent.getUsageTimeSeconds());

        log.info("任务处理完成: {}", taskInfo);

        ChatMessageDto queMessage = AgentUtils.genTaskStatusMessageDto(taskInfo, genContent.getUsageTimeSeconds()) ;
        queMessage.setChatText(genContent.getGenContent());

        queMessage.setBusinessId(genContent.getId());
        queMessage.setRoleId(taskInfo.getRoleId());
        queMessage.setAccountId(taskInfo.getAccountId());
        queMessage.setLoading(false);
        queMessage.setStatus(TaskStatusEnums.COMPLETED.getValue());
        queMessage.setChannelId(taskInfo.getChannelId());

        // 保存到消息记录表中
        long messageId = messageService.saveChatMessage(queMessage , taskInfo.getChannelId());
        queMessage.setBusinessId(messageId);

        // 发送到前端
        sseService.send(chatStreamId, queMessage);
    }

    @Override
    public void handleWorkflowMessageWithoutMessage(MessageTaskInfo taskInfo, WorkflowExecutionDto genContent) {
        taskInfo.setUsageTime(genContent.getUsageTimeSeconds());

        log.info("任务处理完成: {}", taskInfo);

        ChatMessageDto queMessage = AgentUtils.genTaskStatusMessageDto(taskInfo, genContent.getUsageTimeSeconds()) ;
        queMessage.setChatText(genContent.getGenContent());

        queMessage.setBusinessId(genContent.getId());
        queMessage.setRoleId(taskInfo.getRoleId());
        queMessage.setAccountId(taskInfo.getAccountId());
        queMessage.setLoading(false);
        queMessage.setStatus(TaskStatusEnums.COMPLETED.getValue());

        // 保存到消息记录表中
        messageService.saveChatMessage(queMessage , taskInfo.getChannelId());
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

//        // 使用迭代器遍历队列中的每个任务
//        Iterator<MessageTaskInfo> iterator = errorTaskQueue.iterator();
//        while (iterator.hasNext()) {
//            MessageTaskInfo task = iterator.next();
//            // 如果任务的 channelId 与给定的相匹配，则添加到列表中
//            if (String.valueOf(task.getChannelId()).equals(channelId)) {
//                filteredTasks.add(task);
//                // 移除匹配的任务
//                iterator.remove();
//            }
//        }

        return filteredTasks; // 返回匹配的任务列表
    }

}
