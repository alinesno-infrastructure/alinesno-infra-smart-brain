package com.alinesno.infra.smart.assistant.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.entity.MessageQueueConfigEntity;

public interface IMessageQueueConfigService extends IBaseService<MessageQueueConfigEntity> {

    /**
     * 获取到业务ID的任务配置，每个业务的任务都有唯一的任务配置(当然也有重复或者默认的)
     * @return
     */
    MessageQueueConfigEntity queryConfigByBusinessId(String businessId) ;

}
