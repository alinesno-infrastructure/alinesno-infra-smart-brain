package com.alinesno.infra.base.im.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.smart.brain.api.dto.PromptMessageDto;
import com.alinesno.infra.smart.im.dto.ChatMessageDto;
import com.alinesno.infra.smart.im.dto.ChatSendMessageDto;
import com.alinesno.infra.smart.im.dto.WebMessageDto;
import com.alinesno.infra.base.im.mapper.MessageMapper;
import com.alinesno.infra.base.im.utils.MessageFormatter;
import com.alinesno.infra.base.im.utils.TaskUtils;
import com.alinesno.infra.common.core.context.SpringContext;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.entity.MessageEntity;
import com.alinesno.infra.smart.im.enums.MessageType;
import com.alinesno.infra.smart.im.service.IMessageService;
import com.alinesno.infra.smart.im.service.ITaskService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MessageServiceImpl extends IBaseServiceImpl<MessageEntity, MessageMapper> implements IMessageService {

    @Autowired
    private IIndustryRoleService industryRoleService;

    @Override
    public void saveUserMessage(List<WebMessageDto> parsedMessages, Long channelId) {

        // 处理解析后的消息对象
        StringBuilder roleIds = new StringBuilder();

        for (WebMessageDto message : parsedMessages) {
            if (MessageType.MENTION.getValue().equals(message.getType())) {
                roleIds.append(message.getId());
                roleIds.append("\\|");
            }
        }
        StringBuilder chatTextBuilder = new StringBuilder();

        // 假设Content是一个自定义类，包含type、text、username和businessId属性
        for (WebMessageDto content : parsedMessages) {
            if ("text".equals(content.getType())) {
                chatTextBuilder.append("<span class=\"mention-text\">").append(content.getText()).append("</span>");
            } else if ("mention".equals(content.getType())) {
                chatTextBuilder.append("<span class=\"mention\">@").append(content.getUsername()).append("</span>");
            } else if ("business".equals(content.getType())) {
                chatTextBuilder.append("<span class=\"mention-business\">#").append(content.getBusinessId()).append("</span>");
            }
        }
        String chatText = chatTextBuilder.toString();

        MessageEntity msg = new MessageEntity();

        msg.setChannelId(channelId);

        msg.setReaderType("html");
        msg.setRoleType("person");
        msg.setName("软件工程师罗小东");
        msg.setContent(chatText); // JSONObject.toJSONString(parsedMessages));

//        msg.setRoleId(roleIds);
//        msg.setSenderId(IdUtil.getSnowflakeNextId());
//        msg.setMessageId(IdUtil.getSnowflakeNextId());
//        msg.setReceiverId(receiverId.toString());

        this.save(msg);
    }

    @Override
    public List<ChatMessageDto> listByChannelId(String channelId) {

        List<ChatMessageDto> list = new ArrayList<>();

        LambdaQueryWrapper<MessageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageEntity::getChannelId, channelId)
                .orderByAsc(MessageEntity::getAddTime);

        List<MessageEntity> entityList = list(wrapper);

        if (!entityList.isEmpty()) {
            for (MessageEntity e : entityList) {
                ChatMessageDto dto = new ChatMessageDto();

                dto.setChatText(StringUtils.isBlank(e.getFormatContent()) ? e.getContent() : e.getFormatContent());

                dto.setName(e.getName());
                dto.setIcon(e.getIcon());

                dto.setRoleType(e.getRoleType());
                dto.setReaderType(e.getReaderType());
                dto.setBusinessId(e.getBusinessId());
                dto.setDateTime(DateUtil.formatDateTime(e.getAddTime()));

                list.add(dto);
            }
        }

        return list;
    }

    @Deprecated
    @Override
    public void saveChatMessage(List<WebMessageDto> parsedMessages, IndustryRoleEntity roleDto, ChatMessageDto personDto, long channelId, long businessId) {
//        // 处理解析后的消息对象
//        StringBuilder receiverId = new StringBuilder();
//        for (WebMessageDto message : parsedMessages) {
//            if (MessageType.MENTION.getValue().equals(message.getType())) {
//                receiverId.append(message.getId());
//                receiverId.append("\\|");
//            }
//        }
//        StringBuilder chatTextBuilder = new StringBuilder();
//
//        // 假设Content是一个自定义类，包含type、text、username和businessId属性
//        for (WebMessageDto content : parsedMessages) {
//            if ("text".equals(content.getType())) {
//                chatTextBuilder.append("<span class=\"mention-text\">").append(content.getText()).append("</span>");
//            } else if ("mention".equals(content.getType())) {
//                chatTextBuilder.append("<span class=\"mention\">@").append(content.getUsername()).append("</span>");
//            } else if ("business".equals(content.getType())) {
//                chatTextBuilder.append("<span class=\"mention-business\">#").append(content.getBusinessId()).append("</span>");
//            }
//        }
//        MessageEntity msg = getMessageEntity(channelId, chatTextBuilder, receiverId);
//
//        save(msg) ;
//
//
//        MessageEntity entity = new MessageEntity() ;
//
//        entity.setContent(personDto.getChatText().toString()) ;
//
//        entity.setIcon(personDto.getIcon()) ;
//        entity.setName(personDto.getName());
//
//        entity.setRoleType(personDto.getRoleType());
//        entity.setReaderType(personDto.getReaderType());
//        entity.setBusinessId(businessId);
//        entity.setAddTime(new Date()) ;
//
//        entity.setMessageId(IdUtil.getSnowflakeNextId());
//        entity.setChannelId(channelId);
//
//        entity.setSenderId(roleDto.getId()); // 发送者ID
//
//        entity.setReceiverId(receiverId.toString());
//
//        save(entity) ;
    }

    @Override
    public void saveChatMessage(ChatMessageDto personDto, Long channelId) {

        MessageEntity entity = new MessageEntity();

        entity.setContent(personDto.getChatText().toString());
        entity.setFormatContent(personDto.getChatText().toString());
        entity.setName(personDto.getName());
        entity.setRoleType(personDto.getRoleType());
        entity.setReaderType(personDto.getReaderType());
        entity.setBusinessId(IdUtil.getSnowflakeNextId());
        entity.setAddTime(new Date());
        entity.setIcon(personDto.getIcon());

//        entity.setMessageId(IdUtil.getSnowflakeNextId());
//        entity.setMessageId(IdUtil.getSnowflakeNextId());
//        entity.setSenderId(IdUtil.getSnowflakeNextId());
//        entity.setReceiverId(IdUtil.getSnowflakeNextIdStr());

        entity.setChannelId(channelId);
        entity.setRoleId(personDto.getRoleId());
        entity.setAccountId(personDto.getAccountId());

        save(entity);
    }

    @Override
    public void sendUserMessage(ChatSendMessageDto message, List<IndustryRoleEntity> roleList, List<ChatMessageDto> personDto) {

        ITaskService taskService = SpringContext.getBean(ITaskService.class) ;

        // 处理解析后的消息对象
        String ids = message.getUsers().stream().map(String::valueOf).collect(Collectors.joining(","));

        MessageEntity msg = getMessageEntity(
                message.getChannelId(),
                message.getMessage(),
                MessageFormatter.getMessage(message.getMessage()),
                ids
        );

        msg.setAccountId(message.getAccountId());
        save(msg);

        // 保存返回消息
        for (ChatMessageDto dto : personDto) {
            saveChatMessage(dto, message.getChannelId());
        }

        // 触发执行任务
        for (IndustryRoleEntity role : roleList) {
            MessageTaskInfo taskInfo = TaskUtils.genderTaskInfo(message, role);
            taskService.addTask(taskInfo);
        }

    }

    @Override
    public void initChannelHelp(String channelId) {

        long count = count(new LambdaQueryWrapper<MessageEntity>().eq(MessageEntity::getChannelId, channelId));
        if (count == 0) {
            // 完成之后发送消息给前端
            MessageEntity agentInfo = new MessageEntity();

            IndustryRoleEntity defaultHelpAgent = industryRoleService.getDefaultHelpAgent();

            agentInfo.setFormatContent("你好，你可以查看一下使用教程<a target='_blank' href='http://portal.infra.linesno.com'>教程</a>或者@你想咨询的Agent.");
            agentInfo.setName(defaultHelpAgent.getRoleName());
            agentInfo.setIcon(defaultHelpAgent.getRoleAvatar());

            agentInfo.setRoleType("agent");
            agentInfo.setReaderType("html");

            agentInfo.setAddTime(new Date());
            agentInfo.setBusinessId(IdUtil.getSnowflakeNextId());

            agentInfo.setChannelId(Long.valueOf(channelId));
            agentInfo.setRoleId(defaultHelpAgent.getId());

//            agentInfo.setMessageId(IdUtil.getSnowflakeNextId());
//            agentInfo.setSenderId(defaultHelpAgent.getId());
//            agentInfo.setReceiverId(IdUtil.getSnowflakeNextIdStr());

            save(agentInfo);
        }

    }

    @Override
    public List<PromptMessageDto> queryChannelLastMessage(long channel, long accountId, long roleId, int size) {
        LambdaQueryWrapper<MessageEntity> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(MessageEntity::getChannelId, channel)
                .eq(MessageEntity::getRoleId, roleId)
                .eq(MessageEntity::getAccountId, accountId)
                .orderByDesc(MessageEntity::getAddTime)
                .last("limit " + size);

        List<MessageEntity> list = list(queryWrapper);

        return list.stream().map(item -> new PromptMessageDto(item.getRoleType(), item.getFormatContent())).collect(Collectors.toList());
    }

    private static MessageEntity getMessageEntity(Long channelId, String content, String chatText, String receiverId) {
        MessageEntity msg = new MessageEntity();

        msg.setChannelId(channelId);

//        msg.setSenderId(IdUtil.getSnowflakeNextId());
//        msg.setMessageId(IdUtil.getSnowflakeNextId());
//        msg.setReceiverId(receiverId);

        msg.setIcon("1808349839242747906");
        msg.setReaderType("html");
        msg.setRoleType("person");
        msg.setAddTime(new Date());
        msg.setName("软件工程师罗小东");
        msg.setContent(content);
        msg.setFormatContent(chatText);

        return msg;
    }
}
