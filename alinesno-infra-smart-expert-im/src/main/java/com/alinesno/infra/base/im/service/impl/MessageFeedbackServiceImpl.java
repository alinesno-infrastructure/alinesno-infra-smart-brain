package com.alinesno.infra.base.im.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.base.im.mapper.MessageFeedbackMapper;
import com.alinesno.infra.base.im.mapper.MessageMapper;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.im.dto.MessageFeedbackDto;
import com.alinesno.infra.smart.im.entity.MessageEntity;
import com.alinesno.infra.smart.im.entity.MessageFeedbackEntity;
import com.alinesno.infra.smart.im.service.IMessageFeedbackService;
import com.alinesno.infra.smart.im.service.IMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.Date;

/**
 * 消息反馈服务实现类
 */
@Slf4j
@Service
public class MessageFeedbackServiceImpl extends IBaseServiceImpl<MessageFeedbackEntity, MessageFeedbackMapper> implements IMessageFeedbackService {

    private static final int MAX_FEEDBACK_COUNT = 10;

    @Override
    public void messageFeedback(MessageFeedbackDto dto, Long userId, String userName) {

        // 如果当前用户已经对消息进行过10次评价，则不再允许，直接报异常提示
        long count = lambdaQuery()
                .eq(MessageFeedbackEntity::getMessageId, dto.getMessageId())
                .eq(MessageFeedbackEntity::getEvaluatorUserId, userId)
                .count();

        Assert.isTrue(count < MAX_FEEDBACK_COUNT , "当前用户已经对消息进行过"+ MAX_FEEDBACK_COUNT +"次评价，请勿重复评价！");

        MessageFeedbackEntity entity = new MessageFeedbackEntity() ;

        entity.setMessageId(dto.getMessageId()) ;
        entity.setEvaluatorUserId(userId) ;
        entity.setEvaluatorUserName(userName) ;
        entity.setFeel(dto.getFeel()) ;
        entity.setReasons(JSONObject.toJSONString(dto.getReasons())) ;
        entity.setTimestamp(dto.getTimestamp()) ;

        saveOrUpdate(entity);
    }
}
