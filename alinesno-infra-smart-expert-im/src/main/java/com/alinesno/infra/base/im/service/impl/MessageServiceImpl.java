package com.alinesno.infra.base.im.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alinesno.infra.base.im.mapper.MessageMapper;
import com.alinesno.infra.base.im.utils.MessageFormatter;
import com.alinesno.infra.base.im.utils.TaskUtils;
import com.alinesno.infra.common.core.context.SpringContext;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.brain.api.dto.PromptMessageDto;
import com.alinesno.infra.smart.im.dto.ChatMessageDto;
import com.alinesno.infra.smart.im.dto.ChatSendMessageDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.dto.WebMessageDto;
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
import java.util.Objects;
import java.util.stream.Collectors;

import static com.alinesno.infra.smart.im.constants.ImConstants.TYPE_FUNCTION;
import static com.alinesno.infra.smart.im.constants.ImConstants.TYPE_MODIFY;

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
        msg.setContent(chatText);

        this.save(msg);
    }

    @Override
    public List<ChatMessageDto> listByChannelId(String channelId) {

        List<ChatMessageDto> list = new ArrayList<>();

        LambdaQueryWrapper<MessageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageEntity::getChannelId, channelId)
                .orderByDesc(MessageEntity::getAddTime)
                .last("limit 50");  // 查询出最近的50条数据

        List<MessageEntity> entityList = list(wrapper);

        if (!entityList.isEmpty()) {
            int size = entityList.size();

            for(int i = size -1 ; i >= 0; i--){

                MessageEntity e = entityList.get(i);
                ChatMessageDto dto = new ChatMessageDto();

                dto.setChatText(StringUtils.isBlank(e.getFormatContent()) ? e.getContent() : e.getFormatContent());
                dto.setReasoningText(e.getReasoningContent());

                dto.setName(e.getName());
                dto.setIcon(e.getIcon());
                dto.setRoleId(e.getRoleId() == null ? 0L : e.getRoleId());

                dto.setRoleType(e.getRoleType());
                dto.setReaderType(e.getReaderType());
                dto.setBusinessId(e.getId());
                dto.setDateTime(DateUtil.formatDateTime(e.getAddTime()));

                list.add(dto);
            }
        }

        return list;
    }

    @Deprecated
    @Override
    public void saveChatMessage(List<WebMessageDto> parsedMessages, IndustryRoleEntity roleDto, ChatMessageDto personDto, long channelId, long businessId) {
    }

    @Override
    public Long saveChatMessage(ChatMessageDto personDto, Long channelId) {

        MessageEntity entity = new MessageEntity();

        entity.setContent(personDto.getChatText() == null? "" : personDto.getChatText()+"");
        entity.setReasoningContent(personDto.getReasoningText() == null? "" : personDto.getReasoningText());
        entity.setFormatContent(personDto.getChatText() == null ? "" : personDto.getChatText()+"");
        entity.setName(personDto.getName());
        entity.setRoleType(personDto.getRoleType());
        entity.setReaderType(personDto.getReaderType());
        entity.setAddTime(new Date());
        entity.setIcon(personDto.getIcon());

        entity.setChannelId(channelId);
        entity.setRoleId(personDto.getRoleId());
        entity.setAccountId(personDto.getAccountId());

        save(entity);

        return entity.getId() ;
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
                ids ,
                message.getAccountName()
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

            // 执行任务
            if (Objects.equals(TYPE_FUNCTION, message.getType())) {
                taskInfo.setFunctionCall(true);
            } else if (Objects.equals(TYPE_MODIFY, message.getType())) {
                taskInfo.setModify(true);
            }

            // 当前只能处理一条业务消息(TODO 处理多条前端业务消息)
            if(message.getBusinessIds() != null && !message.getBusinessIds().isEmpty()){
                taskInfo.setPreBusinessId(String.valueOf(message.getBusinessIds().get(0)));
            }

            taskService.addTask(taskInfo);
        }

    }

    @Override
    public void initChannelHelp(String channelId , long accountId) {

        long count = count(new LambdaQueryWrapper<MessageEntity>().eq(MessageEntity::getChannelId, channelId));
        if (count == 0) {
            // 完成之后发送消息给前端
            MessageEntity agentInfo = new MessageEntity();

            IndustryRoleEntity defaultHelpAgent = industryRoleService.getDefaultHelpAgent();

            agentInfo.setFormatContent("你好，你可以查看一下使用教程<a target='_blank' href='http://portal.infra.linesno.com'>教程</a>或者@你想咨询的Agent.");

            agentInfo.setName(defaultHelpAgent.getRoleName());
            agentInfo.setIcon(defaultHelpAgent.getRoleAvatar());
            agentInfo.setRoleId(defaultHelpAgent.getId());

            agentInfo.setRoleType("agent");
            agentInfo.setReaderType("html");

            agentInfo.setAccountId(accountId) ;
            agentInfo.setAddTime(new Date());
//            agentInfo.setBusinessId(IdUtil.getSnowflakeNextId());

            agentInfo.setChannelId(Long.valueOf(channelId));

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

    @Override
    public MessageEntity saveMessage(IndustryRoleEntity role, MessageTaskInfo info, String msg) {

        MessageEntity entity = new MessageEntity();

        entity.setContent(msg) ;
        entity.setFormatContent(msg);
        entity.setName(role.getRoleName());

        entity.setRoleType("agent");
        entity.setReaderType("html");

        entity.setAddTime(new Date());
        entity.setIcon(role.getRoleAvatar());

        entity.setChannelId(info.getChannelId());
        entity.setRoleId(role.getId()) ;

        save(entity);
        // 保存流程消息

        return entity ;
    }

    @Override
    public MessageEntity selectByTraceBusId(Long traceBusId) {
        // 根据业务跟踪ID查询消息
        List<MessageEntity> messages = list(new LambdaQueryWrapper<MessageEntity>().eq(MessageEntity::getTraceBusId, traceBusId)) ;
        if (messages.isEmpty()) {
            return null ;
        }
        return getOne(new LambdaQueryWrapper<MessageEntity>().eq(MessageEntity::getTraceBusId, traceBusId));
    }

//    @Override
//    public MessageEntity saveMessage(IndustryRoleEntity role, MessageTaskInfo info, String msg , long messageId) {
//
//        MessageEntity entity = new MessageEntity();
//
//        entity.setContent(msg) ;
//        entity.setFormatContent(msg);
//        entity.setName(role.getRoleName());
//
//        entity.setRoleType("agent");
//        entity.setReaderType("html");
//
//        entity.setAddTime(new Date());
//        entity.setIcon(role.getRoleAvatar());
//
////        entity.setBusinessId(Long.parseLong(info.getBusinessId()));
//        entity.setChannelId(info.getChannelId());
//        entity.setRoleId(role.getId()) ;
//
//        save(entity);
//        // 保存流程消息
//
//        return entity ;
//    }

    private static MessageEntity getMessageEntity(Long channelId, String content, String chatText, String receiverId , String accountName) {
        MessageEntity msg = new MessageEntity();

        msg.setChannelId(channelId);

        msg.setIcon("1808349839242747906");
        msg.setReaderType("html");
        msg.setRoleType("person");
        msg.setAddTime(new Date());
        msg.setName(accountName);
        msg.setContent(content);
        msg.setFormatContent(chatText);

        return msg;
    }
}
