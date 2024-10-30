package com.alinesno.infra.base.im.gateway.provider;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.alinesno.infra.base.im.utils.AgentUtils;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.SuperController;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleCatalogService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.dto.ChatMessageDto;
import com.alinesno.infra.smart.im.dto.ChatSendMessageDto;
import com.alinesno.infra.smart.im.service.IChannelRoleService;
import com.alinesno.infra.smart.im.service.IMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
     * 前端用户发送消息
     * @return
     */
    @PostMapping("/sendUserMessage")
    public AjaxResult sendUserMessage(@RequestBody ChatSendMessageDto message){

        Assert.isTrue(message.getUsers() != null && !message.getUsers().isEmpty(), "请选择处理专家.");

        log.debug("message = {}" , JSONUtil.toJsonPrettyStr(message));
        long currentAccountId = 1L ;

        List<IndustryRoleEntity> roleList = roleService.listByIds(message.getUsers()) ;

        List<ChatMessageDto> personDto = new ArrayList<>() ;
        roleList.forEach(r -> {
            ChatMessageDto msg =  AgentUtils.getChatMessageDto(r, IdUtil.getSnowflakeNextId()) ;
            msg.setAccountId(currentAccountId);
            personDto.add(msg) ;
        });

        message.setAccountId(currentAccountId);
        messageService.sendUserMessage(message , roleList , personDto);

        return AjaxResult.success("操作成功." , personDto) ;
    }

    /**
     * 获取到消息信息
     * @return
     */
    @GetMapping("/chatMessage")
    public AjaxResult chatMessage(String channelId){

        long accountId = 1L ;
        messageService.initChannelHelp(channelId , accountId) ;
        List<ChatMessageDto> chatMessageDtos = messageService.listByChannelId(channelId) ;

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