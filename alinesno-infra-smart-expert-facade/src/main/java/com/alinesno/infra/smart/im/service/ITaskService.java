package com.alinesno.infra.smart.im.service;

import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

/**
 * 提交任务的服务
 */
public interface ITaskService {

    /**
     * 保存任务
     */
    void addTask(@NotNull MessageTaskInfo taskInfo) ;

    /**
     * 更新消息信息
     * @param taskInfo
     * @param genContent
     * @throws IOException
     */
    void handleWorkflowMessage(MessageTaskInfo taskInfo, WorkflowExecutionDto genContent) ;

    /**
     * 更新消息信息
     * @param taskInfo
     * @param genContent
     * @throws IOException
     */
    void handleWorkflowMessageWithoutMessage(MessageTaskInfo taskInfo, WorkflowExecutionDto genContent);

    /**
     * 获取到完成的任务列表
     * @return
     */
    List<MessageTaskInfo> getTaskMessage(MessageTaskInfo taskInfo) ;

    /**
     * 获取到完成的任务列表
     * @param channelId
     * @return
     */
    List<MessageTaskInfo> getTaskMessage(String channelId) ;
}
