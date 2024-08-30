package com.alinesno.infra.base.im.service;

import com.alinesno.infra.base.im.dto.ChatMessageDto;
import com.alinesno.infra.base.im.dto.TableItem;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 提交任务的服务
 */
public interface ITaskService {

    Map<Long , TableItem> flowTaskBox = new HashMap<>() ;

    /**
     * 保存任务
     */
    void addTask(long channelId , long businessId , long roleId , String text, String preBusinessId , IndustryRoleEntity roleDto ) ;

    /**
     * 获取已经完成的任务通知
     * @return
     */
    List<ChatMessageDto> getTaskMessage() ;

    /**
     * 获取到任务执行的通知
     * @return
     */
    List<TableItem> getFlowTaskNotice();
}
