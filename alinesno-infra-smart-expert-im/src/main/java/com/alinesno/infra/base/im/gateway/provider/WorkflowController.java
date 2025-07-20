package com.alinesno.infra.base.im.gateway.provider;

import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.SuperController;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.dto.ChatMessageDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.dto.MessageUpdateDto;
import com.alinesno.infra.smart.im.dto.WorkflowProcessRequestDTO;
import com.alinesno.infra.smart.im.entity.MessageEntity;
import com.alinesno.infra.smart.im.enums.TaskStatusEnums;
import com.alinesno.infra.smart.im.service.IMessageService;
import com.alinesno.infra.smart.im.service.ISSEService;
import com.alinesno.infra.smart.utils.AgentUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
    }

    /**
     * 对文章某段的处理，包括扩写、重写、润色。
     */
    @PostMapping("/processParagraph")
    public AjaxResult processParagraph(@RequestBody @Validated WorkflowProcessRequestDTO dto) {
        log.debug("WorkflowProcessRequestDTO = {}" , dto) ;

        MessageTaskInfo taskInfo = new MessageTaskInfo() ;

        taskInfo.setRoleId(dto.getRoleId());
        taskInfo.setChannelId(dto.getChannelId());
        taskInfo.setText(dto.getAction() +":"+dto.getRequirement()+ ":"+ dto.getModifyContent());
        taskInfo.setModify(true);
        taskInfo.setModifyPreBusinessId(false); ;

        Map<String , Object> paramMap = new java.util.HashMap<>(Map.of("modifyContent", dto.getModifyContent()));
        paramMap.put("action" , dto.getAction()) ;
        paramMap.put("requirement" , dto.getRequirement()) ;

        taskInfo.setParams(paramMap);

        CompletableFuture<WorkflowExecutionDto> genContent  = roleService.runRoleAgent(taskInfo) ;
        log.debug("chatRole = {}" , genContent);

//        return AjaxResult.success("操作成功" , genContent.getGenContent()) ;

        return AjaxResult.success("操作成功") ;
    }


}