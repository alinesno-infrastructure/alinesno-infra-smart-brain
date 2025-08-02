package com.alinesno.infra.smart.im.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.brain.api.dto.PromptMessageDto;
import com.alinesno.infra.smart.im.dto.*;
import com.alinesno.infra.smart.im.entity.MessageEntity;

import java.util.List;

public interface IMessageService extends IBaseService<MessageEntity> {

    /**
     * 保存用户所属频道消息
     * @param parsedMessages
     * @param channelId
     */
    void saveUserMessage(List<WebMessageDto> parsedMessages, Long channelId);

    /**
     * 查询出频道当前所有的消息并转换返回
     * @param channelId
     * @return
     */
    List<ChatMessageDto> listByChannelId(long channelId);

    /**
     * 保存用户的返回信息
     *
     * @param dtoList
     * @param personDto
     * @param channelId
     */
    @Deprecated
    void saveChatMessage(List<WebMessageDto> dtoList, IndustryRoleEntity roleDto , ChatMessageDto personDto, long channelId , long businessId);

    /**
     * 保存消息实体
     *
     * @param personDto
     * @param channelId
     * @return
     */
    Long saveChatMessage(ChatMessageDto personDto, Long channelId);

    /**
     * 用户发送消息给智能体角色
     *
     * @param message
     * @param roleList
     * @param personDto
     */
    void sendUserMessage(ChatSendMessageDto message, List<IndustryRoleEntity> roleList, List<ChatMessageDto> personDto);

    /**
     * 每个频道最开始的Hello World信息
     * @param channelId
     */
    @Deprecated
    void initChannelHelp(String channelId , long accountId);

    /**
     * 查询出最近个人所在频道的消息
     * @param channel
     * @param roleId
     * @return
     */
    List<PromptMessageDto> queryChannelLastMessage(long channel, long accountId ,  long roleId , int size);

    /**
     * 保存消息到数据库中
     * @param role
     * @param info
     * @param msg
     */
    MessageEntity saveMessage(IndustryRoleEntity role, MessageTaskInfo info, String msg);

    /**
     * 根据业务跟踪ID查询消息
     * @param traceBusId
     * @return
     */
    MessageEntity selectByTraceBusId(Long traceBusId);

    /**
     * 每个频道最开始的Hello World信息
     * @param chatMessage
     */
    void initChannelHelp(ChatSendMessageDto chatMessage);

    /**
     * 查询出最近组织所在频道的消息
     * @param chatMessage
     * @return
     */
    List<ChatMessageDto> listByChannel(ChatSendMessageDto chatMessage);

}
