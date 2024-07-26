package com.alinesno.infra.smart.assistant.service;


import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.entity.MessageQueueEntity;

public interface IMessageQueueService extends IBaseService<MessageQueueEntity> {

    /**
     * 添加消息任务列表中
     * @param messageQueue
     */
    void addMessage(MessageQueueEntity messageQueue) ;

    /**
     * 添加消息任务列表中
     * @param messageQueue
     */
    void runMessage(MessageQueueEntity messageQueue) ;

    /**
     * 查询当前消息内容
     * @param businessId
     */
    MessageQueueEntity queryMessage(String businessId) ;

    /**
     * 查询任务状态
     * @return 可构建返回true 否则返回false
     */
    boolean queryQueueStatus(String businessId) ;

    /**
     * 内容生成完成
     *
     * @param businessId
     * @param resultMap
     * @return
     */
    boolean updateAssistantContent(String businessId, String resultMap);

}
