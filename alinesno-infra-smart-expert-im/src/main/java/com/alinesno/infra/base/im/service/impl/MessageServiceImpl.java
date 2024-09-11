package com.alinesno.infra.base.im.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.base.im.dto.ChatMessageDto;
import com.alinesno.infra.base.im.dto.ChatSendMessageDto;
import com.alinesno.infra.base.im.dto.MessageTaskInfo;
import com.alinesno.infra.base.im.dto.WebMessageDto;
import com.alinesno.infra.base.im.entity.MessageEntity;
import com.alinesno.infra.base.im.mapper.MessageMapper;
import com.alinesno.infra.base.im.service.IMessageService;
import com.alinesno.infra.base.im.service.ITaskService;
import com.alinesno.infra.base.im.utils.MessageFormatter;
import com.alinesno.infra.base.im.utils.TaskUtils;
import com.alinesno.infra.common.core.context.SpringContext;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.enums.MessageType;
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
        StringBuilder receiverId = new StringBuilder();
        for (WebMessageDto message : parsedMessages) {
            if (MessageType.MENTION.getValue().equals(message.getType())) {
                receiverId.append(message.getId());
                receiverId.append("\\|");
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

        msg.setMessageId(IdUtil.getSnowflakeNextId());
        msg.setChannelId(channelId);
        msg.setSenderId(IdUtil.getSnowflakeNextId());

        msg.setReaderType("html");
        msg.setRoleType("person");
        msg.setName("软件工程师罗小东");
        msg.setReceiverId(receiverId.toString());
        msg.setContent(chatText); // JSONObject.toJSONString(parsedMessages));

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
        entity.setMessageId(IdUtil.getSnowflakeNextId());

        entity.setMessageId(IdUtil.getSnowflakeNextId());
        entity.setChannelId(channelId);
        entity.setSenderId(IdUtil.getSnowflakeNextId());
        entity.setReceiverId(IdUtil.getSnowflakeNextIdStr());

        save(entity);
    }

    @Override
    public void sendUserMessage(ChatSendMessageDto message, List<IndustryRoleEntity> roleList, List<ChatMessageDto> personDto) {

        ITaskService taskService = SpringContext.getBean(ITaskService.class) ;

        // 生成消息记录
        // 处理解析后的消息对象
        String ids = message.getUsers().stream().map(String::valueOf).collect(Collectors.joining(","));

        MessageEntity msg = getMessageEntity(
                message.getChannelId(),
                message.getMessage(),
                MessageFormatter.getMessage(message.getMessage()),
                ids
        );

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

            agentInfo.setMessageId(IdUtil.getSnowflakeNextId());
            agentInfo.setRoleType("agent");
            agentInfo.setReaderType("html");

            agentInfo.setAddTime(new Date());

            agentInfo.setSenderId(defaultHelpAgent.getId());
            agentInfo.setChannelId(Long.valueOf(channelId));
            agentInfo.setReceiverId(IdUtil.getSnowflakeNextIdStr());
            agentInfo.setBusinessId(IdUtil.getSnowflakeNextId());

            save(agentInfo);
        }

    }

    private static MessageEntity getMessageEntity(Long channelId, String content, String chatText, String receiverId) {
        MessageEntity msg = new MessageEntity();

        msg.setMessageId(IdUtil.getSnowflakeNextId());
        msg.setChannelId(channelId);
        msg.setSenderId(IdUtil.getSnowflakeNextId());

        msg.setIcon("1808349839242747906");
        msg.setReaderType("html");
        msg.setRoleType("person");
        msg.setAddTime(new Date());
        msg.setName("软件工程师罗小东");
        msg.setReceiverId(receiverId);
        msg.setContent(content);
        msg.setFormatContent(chatText);

        return msg;
    }
}
