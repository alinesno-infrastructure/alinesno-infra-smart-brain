package com.alinesno.infra.base.im.gateway.provider;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.base.im.dto.ChatMessageDto;
import com.alinesno.infra.base.im.dto.ChatSendMessageDto;
import com.alinesno.infra.base.im.dto.RobotMessageDto;
import com.alinesno.infra.base.im.entity.UserEntity;
import com.alinesno.infra.base.im.service.IChannelUserService;
import com.alinesno.infra.base.im.service.IMessageService;
import com.alinesno.infra.base.im.service.ITaskService;
import com.alinesno.infra.base.im.utils.AgentUtils;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.SuperController;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.MessageQueueEntity;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleCatalogService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.assistant.service.IMessageQueueService;
import com.alinesno.infra.smart.brain.api.reponse.TaskContentDto;
import com.alinesno.infra.smart.brain.service.IGenerateTaskService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private IMessageQueueService messageQueueService; ;

    @Autowired
    private IMessageService messageService ;

    @Autowired
    private IChannelUserService channelUserService ;

    @Autowired
    private IGenerateTaskService generateTaskService ;

    @Autowired
    private ITaskService taskService ;

    @Autowired
    private IIndustryRoleService roleService ;

    @Autowired
    private IIndustryRoleCatalogService industryRoleCatalogService;

    /**
     * 获取到消息信息
     * @return
     */
    @GetMapping("/chatAssistantContent")
    public AjaxResult chatAssistantContent(long businessId){

        MessageQueueEntity messageQueueDto = messageQueueService.queryMessage(businessId+"") ; //  smartBrainConsumer.chatContent(businessId) ;
        log.debug("chatContent result = {}" , messageQueueDto);

        return AjaxResult.success(messageQueueDto) ;
    }

    /**
     * 前端用户发送消息
     * @return
     */
    @PostMapping("/sendUserMessage")
    public AjaxResult sendUserMessage(@RequestBody ChatSendMessageDto message){

        log.debug("message = {}" , message);
        List<IndustryRoleEntity> roleList = roleService.listByIds(message.getUsers()) ;

        List<ChatMessageDto> personDto = new ArrayList<>() ;
        roleList.forEach(r -> {
            personDto.add(AgentUtils.getChatMessageDto(r, IdUtil.getSnowflakeNextId())) ;
        });

        messageService.sendUserMessage(message , personDto);

        return AjaxResult.success("操作成功." , personDto) ;

//        log.debug("dtoList = {}" , JSONObject.toJSONString(dtoList));
//        String text = AgentUtils.getText(dtoList)  ;
//        long roleId = AgentUtils.getRoleId(dtoList) ;
//        String preBusinessId = AgentUtils.getPreBusinessId(dtoList)  ;
//        long businessId = IdUtil.getSnowflakeNextId(); // 生成一个唯一的业务ID
//
//        IndustryRoleEntity roleDto = roleService.getById(roleId)  ;
//
//        // 提交任务给处理服务，让它后台执行处理
//        taskService.addTask(channelId , businessId , roleId , text , preBusinessId , roleDto);
//
//        ChatMessageDto personDto = AgentUtils.getChatMessageDto(dtoList, roleDto , businessId , text);
//
//        // 保存消息实体
//        // long receiverId = 1L ; // 当前用户的id
//
//        messageService.saveChatMessage(dtoList , roleDto , personDto , channelId , businessId) ;
//        return AjaxResult.success(personDto) ;

    }


    /**
     * 获取到消息信息
     * @return
     */
    @PostMapping("/updateAssistantContent")
    public AjaxResult updateAssistantContent(@RequestBody TaskContentDto dto){

        generateTaskService.modifyContent(dto);

        return ok() ;
    }

    /**
     * 执行下一个节点的任务
     * @return
     */
    @GetMapping("/runChainAgent")
    public AjaxResult runChainAgent(String businessId , String roleId){

        log.debug("businessId = {} , roleId = {}" , businessId , roleId);
        TaskContentDto dto = generateTaskService.getContentByBusinessId(businessId) ;

        log.debug("chatContent result = {}" , dto);

//        String resultData = result.get("data").toString() ;
//        TaskContentDto ta = null ;
//        if(resultData != null) {
//            ta = JSONObject.parseObject(resultData, TaskContentDto.class);
//        }
//        smartAssistantConsumer.runChainAgent(ta , roleId) ;

        return ok() ;
    }

    /**
     * 轮训任务结果，这里假设并发不高的情况下执行
     * TODO 后续再进一步优化
     * @return
     */
    @GetMapping("/getTaskNotice")
    public AjaxResult getTaskNotice(){
        return AjaxResult.success("查询成功" , taskService.getTaskMessage()) ;
    }

    /**
     * 轮训任务运行状态，这里假设并发不高的情况下执行
     * TODO 后续再进一步优化
     * @return
     */
    @GetMapping("/getFlowTaskNotice")
    public AjaxResult getFlowTaskNotice(){
        return AjaxResult.success("查询成功" , taskService.getFlowTaskNotice()) ;
    }

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

        List<UserEntity> userEntities = channelUserService.getChannelAgent(channelId) ;

        return AjaxResult.success(userEntities) ;
    }


    /**
     * 获取所有频道列表
     * @return
     */
    @GetMapping("/getAllCatalog")
    public AjaxResult getAllCatalog(){
        return AjaxResult.success(industryRoleCatalogService.allCatalog()) ;
    }


    /**
     * 发送消息给钉钉
     * @return
     */
    @SneakyThrows
    @GetMapping("/send")
    public AjaxResult robotSend() {

        RobotMessageDto dto = new RobotMessageDto() ;

        dto.setAtAll(false);
        dto.setMessageType("markdown");
        dto.setMessageContent("你要求生成的Java计算机考核题目已经生成，请查看连接.");

        // smartAssistantConsumer.sendMessageWebhook(dto, null) ;

        return ok() ;
    }



}