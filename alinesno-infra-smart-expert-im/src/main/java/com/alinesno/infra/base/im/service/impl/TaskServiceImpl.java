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
    public void addTask(MessageTaskInfo taskInfo) {
        CompletableFuture<WorkflowExecutionDto> genContent  = roleService.runRoleAgent(taskInfo) ;

        // 处理消息结果
        genContent.whenComplete((result, ex) -> {
            handleWorkflowMessage(taskInfo, result) ;
        }) ;
    }

    @SneakyThrows
    @Override
    public void handleWorkflowMessage(MessageTaskInfo taskInfo, WorkflowExecutionDto genContent) {

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
        if(genContent.isPrint()){
            sseService.send(chatStreamId, queMessage);
        }else{
            sseService.sendDone(chatStreamId) ;
        }
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
