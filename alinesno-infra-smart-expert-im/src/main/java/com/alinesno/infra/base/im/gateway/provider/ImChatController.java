package com.alinesno.infra.base.im.gateway.provider;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.base.dto.ManagerAccountDto;
import com.alinesno.infra.common.web.adapter.login.account.CurrentAccountJwt;
import com.alinesno.infra.common.web.adapter.rest.SuperController;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleCatalogService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.dto.ChatMessageDto;
import com.alinesno.infra.smart.im.dto.ChatSendMessageDto;
import com.alinesno.infra.smart.im.dto.SayHelloDto;
import com.alinesno.infra.smart.im.entity.MessageEntity;
import com.alinesno.infra.smart.im.service.IChannelRoleService;
import com.alinesno.infra.smart.im.service.IMessageService;
import com.alinesno.infra.smart.utils.AgentUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 */
@Slf4j
@RestController
@RequestMapping(value = "/v1/api/infra/base/im/chat/")
public class ImChatController extends SuperController {

    @Autowired
    private IMessageService messageService ;

    @Autowired
    private IChannelRoleService channelUserService ;

    @Autowired
    private IIndustryRoleService roleService ;

    @Autowired
    private IIndustryRoleCatalogService roleCatalogService;


    /**
     * 获取到消息信息
     * @return
     */
    @GetMapping("/chatAssistantContent")
    public AjaxResult chatAssistantContent(long businessId){
        return AjaxResult.success();
    }

    /**
     * 进入群聊的打招呼语言
     */
    @DataPermissionSave
    @PostMapping("/chanelSayHello")
    public AjaxResult chanelSayHello(@RequestBody SayHelloDto role , long channelId){

        MessageEntity entity = new MessageEntity();

        entity.setContent(role.getGreeting()) ;
        entity.setFormatContent(role.getGreeting());
        entity.setName(role.getRoleName());

        entity.setRoleType("agent");
        entity.setReaderType("html");

        entity.setAddTime(new Date());
        entity.setIcon(role.getRoleAvatar());

        entity.setChannelId(channelId) ;
        entity.setRoleId(role.getId()) ;

        entity.setOrgId(role.getOrgId());
        entity.setDepartmentId(role.getDepartmentId());
        entity.setOperatorId(role.getOperatorId());

        messageService.save(entity);

        return AjaxResult.success();
    }

    /**
     * 前端用户发送消息
     * @return
     */
    @DataPermissionSave
    @PostMapping("/sendUserMessage")
    public AjaxResult sendUserMessage(@RequestBody ChatSendMessageDto message){

        Assert.isTrue(message.getUsers() != null && !message.getUsers().isEmpty(), "请选择处理专家.");

        log.debug("message = {}" , JSONUtil.toJsonPrettyStr(message));
        ManagerAccountDto managerAccount = CurrentAccountJwt.get();

        List<IndustryRoleEntity> roleList = roleService.listByIds(message.getUsers()) ;

        List<ChatMessageDto> personDto = new ArrayList<>() ;
        roleList.forEach(r -> {
            ChatMessageDto msg =  AgentUtils.getChatMessageDto(r, IdUtil.getSnowflakeNextId()) ;
            msg.setAccountId(managerAccount.getId());
            personDto.add(msg) ;
        });

        // 设置账户信息
        message.setAccountId(managerAccount.getId());
        message.setAccountName(managerAccount.getName());
        message.setAccountEmail(managerAccount.getEmail());
        message.setAccountIcon(managerAccount.getAvatarPath());

        messageService.sendUserMessage(message , roleList , personDto);

        return AjaxResult.success("操作成功.") ;
    }

    /**
     * 获取到消息信息
     * @return
     */
    @DataPermissionSave
    @GetMapping("/chatMessage")
    public AjaxResult chatMessage(ChatSendMessageDto chatMessage , @RequestParam Long channelId){

        chatMessage.setChannelId(channelId);
        messageService.initChannelHelp(chatMessage) ;

        List<ChatMessageDto> chatMessageDtos = messageService.listByChannel(chatMessage) ;
        return AjaxResult.success(chatMessageDtos) ;
    }

    /**
     * 获取到频道的Agent列表
     * @return
     */
    @GetMapping("/getChannelAgent")
    public AjaxResult getChannelAgent(String channelId){
        List<IndustryRoleEntity> userEntities = channelUserService.getChannelAgent(channelId) ;
        return AjaxResult.success(userEntities) ;
    }


    /**
     * 获取所有频道列表
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/getAllCatalog")
    public AjaxResult getAllCatalog(PermissionQuery query){
        return AjaxResult.success(roleCatalogService.allCatalog(query)) ;
    }

}