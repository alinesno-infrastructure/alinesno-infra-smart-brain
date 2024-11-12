package com.alinesno.infra.base.im.gateway.provider;

import cn.hutool.core.util.IdUtil;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alinesno.infra.smart.utils.AgentUtils;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.login.account.CurrentAccountJwt;
import com.alinesno.infra.common.web.adapter.rest.SuperController;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.role.llm.QianWenLLM;
import com.alinesno.infra.smart.assistant.role.llm.adapter.MessageManager;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.dto.ChatMessageDto;
import com.alinesno.infra.smart.im.dto.ChatSendMessageDto;
import com.alinesno.infra.smart.im.service.IMessageService;
import com.alinesno.infra.smart.im.service.ISSEService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 使用于多个网站针对于专家单独咨询的接口
 */
@Slf4j
@RestController
@RequestMapping(value = "/v1/api/infra/base/im/roleChat/")
public class RoleChatController extends SuperController {

//    @Autowired
//    private IRoleChatService roleChatService;

    @Autowired
    private IMessageService messageService;

    @Autowired
    private IIndustryRoleService industryRoleService;

    @Autowired
    private QianWenLLM qianWenLLM;

    @Autowired
    private ISSEService sseService;

    /**
     * 获取角色信息
     *
     * @param roleId
     * @return
     */
    @SneakyThrows
    @GetMapping("/getInfo")
    public AjaxResult getInfo(long roleId) {

        IndustryRoleEntity role = industryRoleService.getById(roleId);
        ChatMessageDto message = AgentUtils.getChatMessageDto(role, IdUtil.getSnowflakeNextId());

        // 生成介绍的chat
        Message userMsg = getMessage(role);

        MessageManager msgManager = new MessageManager(10);
        msgManager.add(userMsg);

        StringBuilder stringBuilder = qianWenLLM.chatComponent(msgManager);
        message.setChatText(stringBuilder.toString());
        message.setLoading(false);

        AjaxResult result = AjaxResult.success();

        result.put("role", role);
        result.put("message", message);

        return result;
    }

    private static Message getMessage(IndustryRoleEntity role) {
        String introBuilder = "你是一名[" +
                role.getRoleName() +
                "], " +
                role.getRoleLevel() +
                "，" +
                role.getResponsibilities() +
                "，现在有人咨询你，请你生成一句跟别人打招呼的语句，体现你的业务能力。";

        return Message
                .builder()
                .role(Role.USER.getValue())
                .content(introBuilder)
                .build();
    }

    /**
     * 角色对话
     *
     * @param chatMessage
     * @param roleId
     * @return
     */
    @SneakyThrows
    @PostMapping("/chatRole")
    public AjaxResult chatRole(@RequestBody ChatSendMessageDto chatMessage, long roleId) {

        IndustryRoleEntity role = industryRoleService.getById(roleId);

        long currentAccountId = CurrentAccountJwt.getUserId();

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

        ChatMessageDto msgDto = AgentUtils.getChatMessageDto(role, IdUtil.getSnowflakeNextId());

        msgDto.setChatText(chatMessage.getMessage());
        msgDto.setName(CurrentAccountJwt.get().getName());
        msgDto.setRoleType("person");
        msgDto.setIcon("1808350003370057729");

        return AjaxResult.success(msgDto);
    }

}
