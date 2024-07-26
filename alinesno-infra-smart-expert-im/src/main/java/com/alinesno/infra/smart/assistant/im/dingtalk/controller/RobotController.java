package com.alinesno.infra.smart.assistant.im.dingtalk.controller;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.SuperController;
import com.alinesno.infra.smart.assistant.adapter.SmartBrainConsumer;
import com.alinesno.infra.smart.assistant.api.adapter.TaskContentDto;
import com.alinesno.infra.smart.assistant.im.dingtalk.dto.ChatMessageDto;
import com.alinesno.infra.smart.assistant.im.dingtalk.dto.DingtalkRobotMessageDto;
import com.alinesno.infra.smart.assistant.im.dingtalk.dto.WebMessageDto;
import com.alinesno.infra.smart.assistant.im.dingtalk.event.DingtalkMsgDispatcher;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 参考相关资料：https://open.dingtalk.com/document/orgapp/robot-reply-and-send-messages
 */
@Slf4j
@RestController
@RequestMapping(value = "/v1/smart/assistant/dingtalk/robot/")
public class RobotController extends SuperController {

    @Autowired
    private DingtalkMsgDispatcher dingtalkMsgDispatcher ;

    @Autowired
    private IIndustryRoleService industryRoleService ;

    @Autowired
    private SmartBrainConsumer smartBrainConsumer ;

    /**
     * 获取到消息信息
     * @return
     */
    @GetMapping("/chatAssistantContent")
    public AjaxResult chatAssistantContent(String businessId){

        AjaxResult result = smartBrainConsumer.chatContent(businessId) ;
        log.debug("chatContent result = {}" , result);

        return result ;
    }

    /**
     * 前端用户发送消息
     * @return
     */
    @PostMapping("/sendUserMessage")
    public AjaxResult sendUserMessage(@RequestBody List<WebMessageDto> dtoList){

        log.debug("dtoList = {}" , JSONObject.toJSONString(dtoList));

        ChatMessageDto personDto = new ChatMessageDto() ;

        personDto.setChatText("收到，罗小东的任务我已经在处理，请稍等1-2分钟 :-)");
        personDto.setName("考核题目生成Agent");
        personDto.setRoleType("agent");
        personDto.setDateTime(DateUtil.formatDateTime(new Date()));

        return AjaxResult.success(personDto) ;
    }

    /**
     * 获取到消息信息
     * @return
     */
    @PostMapping("/updateAssistantContent")
    public AjaxResult updateAssistantContent(@RequestBody TaskContentDto dto){

        AjaxResult result = smartBrainConsumer.modifyContent(dto) ;
        log.debug("chatContent result = {}" , result);

        return result ;
    }

    /**
     * 执行下一个节点的任务
     * @return
     */
    @GetMapping("/runChainAgent")
    public AjaxResult runChainAgent(String businessId , long roleId){

        log.debug("businessId = {} , roleId = {}" , businessId , roleId);
        AjaxResult result = smartBrainConsumer.chatContent(businessId) ;

        log.debug("chatContent result = {}" , result);

        String resultData = result.get("data").toString() ;
        TaskContentDto ta = null ;
        if(resultData != null) {
            ta = JSONObject.parseObject(resultData, TaskContentDto.class);
        }

        industryRoleService.runChainAgent(ta , roleId) ;

        return ok() ;
    }

    /**
     * 获取到消息信息
     * @return
     */
    @GetMapping("/chatMessage")
    public AjaxResult chatMessage(String businessId){

        AjaxResult result = smartBrainConsumer.chatContent(businessId) ;

        log.debug("chatContent result = {}" , result);

        String resultData = result.get("data").toString() ;
        TaskContentDto ta = null ;
        if(resultData != null){
            ta =  JSONObject.parseObject(resultData, TaskContentDto.class) ;
            List<ChatMessageDto> messageList = new ArrayList<>() ;


            ChatMessageDto dto = new ChatMessageDto() ;
            dto.setChatText(ta.getGenContent());
            dto.setName("高级数据库工程师");
            dto.setRoleType("agent");
            dto.setDateTime(DateUtil.formatDateTime(new Date()));
            messageList.add(dto) ;

            ChatMessageDto dto3 = new ChatMessageDto() ;

            String md = "" +
                    "### 罗小东的任务已经处理\n" +
//                    "- 任务：\n" + ta.getGenContent() + "\n" +
                    "- 任务: \n" + "请编写关于Ansible的考核题目." + "\n" +
                    "- 业务标识: 1733539703232249856\n" +
                    "- 持续时间: 46秒503\n" +
                    "- 环境: [测试环境](#)\n" +
                    "- 内容: [查看生成结果](http://localhost/smart/specialist/index?businessId=1733539703232249856)\n" +
                    "- 状态: 完成\n" +
                    "- 完成时间: 2023-12-10 01:31:34\n" +
                    "- 执行人：培训题设计Agent" +
                    "" ;

            dto3.setChatText(md);
            dto3.setName("高级数据库工程师");
            dto3.setRoleType("agent");
            dto3.setDateTime(DateUtil.formatDateTime(new Date()));
            messageList.add(dto3) ;

            ChatMessageDto personDto1 = new ChatMessageDto() ;
            personDto1.setChatText("生成一个开发管理服务");
            personDto1.setName("考核题目生成Agent");
            personDto1.setRoleType("person");
            personDto1.setDateTime(DateUtil.formatDateTime(new Date()));
            messageList.add(personDto1) ;

            ChatMessageDto personDto = new ChatMessageDto() ;
            personDto.setChatText("收到，罗小东的任务我已经在处理，请稍等1-2分钟 :-)");
            personDto.setName("考核题目生成Agent");
            personDto.setRoleType("agent");
            personDto.setDateTime(DateUtil.formatDateTime(new Date()));
            messageList.add(personDto) ;

            return AjaxResult.success(messageList) ;
        }

        return AjaxResult.error() ;
    }

    /**
     * 发送消息给钉钉
     * @return
     */
    @SneakyThrows
    @GetMapping("/send")
    public AjaxResult robotSend() {

        DingtalkRobotMessageDto dto = new DingtalkRobotMessageDto() ;

        dto.setAtAll(false);
        dto.setMessageType("markdown");
        dto.setMessageContent("你要求生成的Java计算机考核题目已经生成，请查看连接.");

        dingtalkMsgDispatcher.sendMessageWebhook(dto, null) ;

        return ok() ;
    }

}