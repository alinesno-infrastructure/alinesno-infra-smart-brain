package com.alinesno.infra.smart.assistant.queue.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.entity.MessageQueueConfigEntity;
import com.alinesno.infra.smart.assistant.queue.mapper.MessageQueueConfigMapper;
import com.alinesno.infra.smart.assistant.service.IMessageQueueConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageQueueConfigServiceImpl extends IBaseServiceImpl<MessageQueueConfigEntity, MessageQueueConfigMapper> implements IMessageQueueConfigService {

    @Override
    public MessageQueueConfigEntity queryConfigByBusinessId(String businessId) {

        if(StringUtils.isBlank(businessId)){  // 如果为空，则返回默认的任务配置ID
            MessageQueueConfigEntity config = new MessageQueueConfigEntity() ;

            // 默认配置值
            config.setMaxQueueSize(100);
            config.setMessageTimeout(30*60*60);
            config.setMaxRetryAttempts(5);

            return config ;
        }

        LambdaQueryWrapper<MessageQueueConfigEntity> wrapper = new LambdaQueryWrapper<>() ;
        wrapper.eq(MessageQueueConfigEntity::getBusinessId , businessId) ;

        return getOne(wrapper);
    }
}
