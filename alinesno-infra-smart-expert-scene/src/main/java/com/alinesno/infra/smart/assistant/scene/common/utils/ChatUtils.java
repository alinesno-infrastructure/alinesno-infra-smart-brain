package com.alinesno.infra.smart.assistant.scene.common.utils;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.dto.ChatMessageDto;
import com.alinesno.infra.smart.im.dto.ChatSendMessageDto;
import com.alinesno.infra.smart.im.service.IMessageService;
import com.alinesno.infra.smart.utils.AgentUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 聊天工具类
 */
@Component
@Slf4j
public class ChatUtils {

    @Autowired
    private IMessageService messageService;

    @Autowired
    private IIndustryRoleService industryRoleService;

    /**
     * 角色聊天
     * @param chatMessage
     * @param roleId
     * @param currentAccountId
     */
    public void chatRole(ChatSendMessageDto chatMessage, long roleId , long currentAccountId){

        IndustryRoleEntity role = industryRoleService.getById(roleId);
        List<IndustryRoleEntity> roleList = new ArrayList<>();
        roleList.add(role);

        List<ChatMessageDto> personDto = new ArrayList<>();
        roleList.forEach(r -> {
            ChatMessageDto msg = AgentUtils.getChatMessageDto(r, IdUtil.getSnowflakeNextId());
            msg.setAccountId(currentAccountId);
            personDto.add(msg);
        });

        chatMessage.setUsers(Collections.singletonList(roleId));
        chatMessage.setAccountId(currentAccountId);

        messageService.sendUserMessage(chatMessage, roleList, personDto);
    }

}
