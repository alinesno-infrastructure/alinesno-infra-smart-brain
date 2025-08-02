package com.alinesno.infra.smart.im.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.im.dto.ParserDataBean;
import com.alinesno.infra.smart.im.entity.AccountChannelEntity;
import com.alinesno.infra.smart.im.entity.MessageReferenceEntity;

import java.util.List;

public interface IMessageReferenceService extends IBaseService<MessageReferenceEntity> {

    /**
     * 保存消息的引用信息
     *
     * @param datasetMap 引用数据ID
     * @param messageId 消息id
     */
    void saveMessageReference(List<ParserDataBean> datasetMap, String messageId);

}
