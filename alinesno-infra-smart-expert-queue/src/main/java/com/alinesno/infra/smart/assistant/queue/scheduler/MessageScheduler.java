package com.alinesno.infra.smart.assistant.queue.scheduler;

import com.alinesno.infra.smart.assistant.entity.MessageQueueEntity;
import com.alinesno.infra.smart.assistant.enums.MessageStatus;
import com.alinesno.infra.smart.assistant.service.IMessageQueueService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 消息任务处理
 */
@Component
public class MessageScheduler {

    @Autowired
    private IMessageQueueService messageQueueService ;

    /**
     * 任务循环，查询队列状态和添加最新的任务
     */
    @Scheduled(initialDelay = 10000 , fixedDelay = 60000)
    public void jobQueueLoop(){

        LambdaQueryWrapper<MessageQueueEntity> wrapper = new LambdaQueryWrapper<>() ;
        wrapper.eq(MessageQueueEntity::getStatus , MessageStatus.PENDING.getValue()) ;

        List<MessageQueueEntity> messageQueueEntities =  messageQueueService.list(wrapper) ;

        for(MessageQueueEntity messageQueue : messageQueueEntities){
            messageQueueService.runMessage(messageQueue);
        }

    }

    /**
     * 清理超时的任务
     */
    @Scheduled(initialDelay = 30000 , fixedDelay = 60000)
    public void jobOutTimeLoop(){

    }

}
