package com.alinesno.infra.base.im.gateway.provider;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.alinesno.infra.smart.im.dto.ChatMessageDto;
import com.alinesno.infra.smart.im.dto.ChatSendMessageDto;
import com.alinesno.infra.base.im.utils.AgentUtils;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.SuperController;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleCatalogService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
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

//    @Autowired
//    private IMessageQueueService messageQueueService; ;

    @Autowired
    private IMessageService messageService ;

    @Autowired
    private IChannelRoleService channelUserService ;

//    @Autowired
//    private IGenerateTaskService generateTaskService ;

//    @Autowired
//    private ITaskService taskService ;

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

//        MessageQueueEntity messageQueueDto = messageQueueService.queryMessage(businessId+"") ; //  smartBrainConsumer.chatContent(businessId) ;
//        log.debug("chatContent result = {}" , messageQueueDto);
//
//        return AjaxResult.success(messageQueueDto) ;

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


//    /**
//     * 获取到消息信息
//     * @return
//     */
//    @PostMapping("/updateAssistantContent")
//    public AjaxResult updateAssistantContent(@RequestBody TaskContentDto dto){
//
////        generateTaskService.modifyContent(dto);
//
//        return ok() ;
//    }

//    /**
//     * 执行下一个节点的任务
//     * @return
//     */
//    @GetMapping("/runChainAgent")
//    public AjaxResult runChainAgent(String businessId , String roleId){
//
//        log.debug("businessId = {} , roleId = {}" , businessId , roleId);
//
////        TaskContentDto dto = generateTaskService.getContentByBusinessId(businessId) ;
////        log.debug("chatContent result = {}" , dto);
//
////        String resultData = result.get("data").toString() ;
////        TaskContentDto ta = null ;
////        if(resultData != null) {
////            ta = JSONObject.parseObject(resultData, TaskContentDto.class);
////        }
////        smartAssistantConsumer.runChainAgent(ta , roleId) ;
//
//        return ok() ;
//    }

//    /**
//     * 轮训任务结果，这里假设并发不高的情况下执行
//     * TODO 后续再进一步优化
//     * @return
//     */
//    @GetMapping("/getTaskNotice")
//    public AjaxResult getTaskNotice(){
////        return AjaxResult.success("查询成功" , taskService.getTaskMessage()) ;
//
//        return AjaxResult.success();
//    }

//    /**
//     * 轮训任务运行状态，这里假设并发不高的情况下执行
//     * TODO 后续再进一步优化
//     * @return
//     */
//    @GetMapping("/getFlowTaskNotice")
//    public AjaxResult getFlowTaskNotice(){
////        return AjaxResult.success("查询成功" , taskService.getFlowTaskNotice()) ;
//
//        return AjaxResult.success();
//    }

    /**
     * 获取到消息信息
     * @return
     */
    @GetMapping("/chatMessage")
    public AjaxResult chatMessage(String channelId){

        messageService.initChannelHelp(channelId) ;
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
    @GetMapping("/getAllCatalog")
    public AjaxResult getAllCatalog(){
        return AjaxResult.success(roleCatalogService.allCatalog()) ;
    }


//    /**
//     * 发送消息给钉钉
//     * @return
//     */
//    @SneakyThrows
//    @GetMapping("/send")
//    public AjaxResult robotSend() {
//
//        RobotMessageDto dto = new RobotMessageDto() ;
//
//        dto.setAtAll(false);
//        dto.setMessageType("markdown");
//        dto.setMessageContent("你要求生成的Java计算机考核题目已经生成，请查看连接.");
//
//        // smartAssistantConsumer.sendMessageWebhook(dto, null) ;
//
//        return ok() ;
//    }

}