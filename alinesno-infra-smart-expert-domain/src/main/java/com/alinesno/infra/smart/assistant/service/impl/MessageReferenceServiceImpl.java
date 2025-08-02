package com.alinesno.infra.smart.assistant.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.mapper.AgentSceneMapper;
import com.alinesno.infra.smart.assistant.mapper.MessageReferenceMapper;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.constants.AgentConstants;
import com.alinesno.infra.smart.im.dto.ParserDataBean;
import com.alinesno.infra.smart.im.entity.AgentSceneEntity;
import com.alinesno.infra.smart.im.entity.MessageReferenceEntity;
import com.alinesno.infra.smart.im.service.IAgentSceneService;
import com.alinesno.infra.smart.im.service.IMessageReferenceService;
import com.alinesno.infra.smart.scene.dto.SceneAgent;
import com.alinesno.infra.smart.scene.dto.SceneInfoDto;
import com.alinesno.infra.smart.scene.enums.SceneEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 消息引用实体对象
 */
@Slf4j
@Service
public class MessageReferenceServiceImpl extends IBaseServiceImpl<MessageReferenceEntity, MessageReferenceMapper> implements IMessageReferenceService {

    /**
     * 保存消息引用
     * @param datasetMap 引用数据ID
     * @param messageId 消息id
     */
    @Override
    public void saveMessageReference(List<ParserDataBean> datasetMap, String messageId) {

        if(datasetMap != null){
            List<MessageReferenceEntity> messageReferenceList = new ArrayList<>();

            for (ParserDataBean parserDataBean : datasetMap) {
                MessageReferenceEntity messageReferenceEntity = new MessageReferenceEntity();

                messageReferenceEntity.setMessageId(messageId);
                messageReferenceEntity.setSort(parserDataBean.getSort());
                messageReferenceEntity.setName(parserDataBean.getName());
                messageReferenceEntity.setType(parserDataBean.getType());
                messageReferenceEntity.setTextLength(parserDataBean.getTextLength());
                messageReferenceEntity.setContent(parserDataBean.getContent());

                messageReferenceList.add(messageReferenceEntity);
            }

            saveBatch(messageReferenceList);
        }

    }
}
