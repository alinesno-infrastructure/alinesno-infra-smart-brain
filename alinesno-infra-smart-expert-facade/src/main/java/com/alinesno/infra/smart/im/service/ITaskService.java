package com.alinesno.infra.smart.im.service;

import com.alinesno.infra.smart.im.dto.MessageTaskInfo;

import javax.validation.constraints.NotNull;
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
