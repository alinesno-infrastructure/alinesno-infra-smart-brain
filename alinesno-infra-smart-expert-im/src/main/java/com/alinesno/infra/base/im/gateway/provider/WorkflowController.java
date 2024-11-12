package com.alinesno.infra.base.im.gateway.provider;

import com.alinesno.infra.smart.utils.AgentUtils;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.SuperController;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.dto.ChatMessageDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.dto.MessageUpdateDto;
import com.alinesno.infra.smart.im.entity.MessageEntity;
import com.alinesno.infra.smart.im.enums.TaskStatusEnums;
import com.alinesno.infra.smart.im.service.IMessageService;
import com.alinesno.infra.smart.im.service.ISSEService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;

/**
 * 应用构建Controller
 * 处理与ApplicationEntity相关的请求
 * 继承自BaseController类并实现IApplicationService接口
 *
 * @version 1.0.0
 * @author luoxiaodong
 */
@Slf4j
@RestController
@Scope("prototype")
@RequestMapping(value = "/v1/api/infra/base/im/workflow")
public class WorkflowController extends SuperController {

    @Autowired
    private IMessageService messageService;

    @Autowired
    private IIndustryRoleService roleService ;

    @Autowired
    private ISSEService sseService;

    /**
     * 根据业务id获取到消息内容
     */
    @GetMapping("/detail")
    public AjaxResult getWorkflowExecution(@RequestParam String messageId) {

//        WorkflowExecutionDto dto = workflowService.getWorkflowExecution(businessId) ;

        MessageEntity dto = messageService.getById(messageId) ;
        return AjaxResult.success(dto) ;
    }

    /**
     * 更新消息内容
     */
    @SneakyThrows
    @PostMapping("/updateContent")
    public AjaxResult updateContent(@RequestBody MessageUpdateDto dto) {

        log.debug("updateContent:{}", dto);

        MessageEntity message = messageService.getById(dto.getBusinessId());
        IndustryRoleEntity role = roleService.getById(message.getRoleId());

        // 添加到消息记录表中
        MessageTaskInfo taskInfo = new MessageTaskInfo();

        taskInfo.setRoleId(message.getRoleId());
        taskInfo.setChannelId(message.getChannelId());
        // taskInfo.setUsageTime(message.get);
        taskInfo.setRoleDto(role);

        log.info("任务处理完成: {}", taskInfo);

        ChatMessageDto queMessage = AgentUtils.genTaskStatusMessageDto(taskInfo , null) ;
        queMessage.setChatText(dto.getContent());

        queMessage.setRoleId(taskInfo.getRoleId());
        queMessage.setAccountId(taskInfo.getAccountId());
        queMessage.setLoading(false);
        queMessage.setStatus(TaskStatusEnums.COMPLETED.getValue());

        // 保存到消息记录表中
        Long newMessageId = messageService.saveChatMessage(queMessage , taskInfo.getChannelId());

        queMessage.setBusinessId(newMessageId);
        sseService.send(String.valueOf(taskInfo.getChannelId()), queMessage);

        return ok() ;

//        WorkflowExecutionEntity workflowExecution = workflowExecutionService.getById(dto.getBusinessId());
//        workflowExecution.setId(null); // 变成新的消息进行保存
//
//        if(Boolean.parseBoolean(dto.getCode())){
//            workflowExecution.setGenContent(formatCodeBlack(dto.getContent()));
//        }else{
//            workflowExecution.setGenContent(dto.getContent().toString());
//        }
//
//        workflowExecutionService.save(workflowExecution) ;
//
//        IndustryRoleEntity role = roleService.getById(workflowExecution.getRoleId());
//
//        // 添加到消息记录表中
//        MessageTaskInfo taskInfo = new MessageTaskInfo();
//
//        taskInfo.setRoleId(workflowExecution.getRoleId());
//        taskInfo.setChannelId(workflowExecution.getChannelId());
//        taskInfo.setUsageTime(workflowExecution.getUsageTimeSeconds());
//        taskInfo.setRoleDto(role);
//
//        log.info("任务处理完成: {}", taskInfo);
//
//        ChatMessageDto queMessage = AgentUtils.genTaskStatusMessageDto(taskInfo , workflowExecution.getUsageTimeSeconds()) ;
//
//        if(Boolean.parseBoolean(dto.getCode())){
//            queMessage.setChatText(formatCodeBlack(dto.getContent()));
//        }else{
//            queMessage.setChatText(dto.getContent());
//        }
//
//        queMessage.setBusinessId(workflowExecution.getId());
//        queMessage.setRoleId(taskInfo.getRoleId());
//        queMessage.setAccountId(taskInfo.getAccountId());
//        queMessage.setLoading(false);
//        queMessage.setStatus(TaskStatusEnums.COMPLETED.getValue());
//
//        sseService.send(String.valueOf(taskInfo.getChannelId()), queMessage);
//
//        // 保存到消息记录表中
//        messageService.saveChatMessage(queMessage , taskInfo.getChannelId());

//        return ok() ;
    }

}