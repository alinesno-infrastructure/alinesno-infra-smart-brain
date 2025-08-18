package com.alinesno.infra.smart.im.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.brain.api.dto.PromptMessageDto;
import com.alinesno.infra.smart.im.dto.*;
import com.alinesno.infra.smart.im.entity.MessageEntity;
import com.alinesno.infra.smart.im.entity.MessageFeedbackEntity;

import java.util.ArrayList;
import java.util.List;

public interface IMessageFeedbackService extends IBaseService<MessageFeedbackEntity> {

    /**
     * 消息反馈
     * @param dto
     */
    void messageFeedback(MessageFeedbackDto dto , Long userId , String userName);

    /**
     * 统计角色反馈
     * @param longs
     * @return
     */
    List<RoleFeedbackStat> countFeedbackByRoleIds(ArrayList<Long> longs);
}
