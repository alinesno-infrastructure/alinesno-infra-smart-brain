package com.alinesno.infra.base.im.service.impl;

import cn.hutool.json.JSONUtil;
import com.alinesno.infra.base.im.service.IWorkflowService;
import com.alinesno.infra.base.im.utils.AgentUtils;
import com.alinesno.infra.smart.assistant.api.CodeContent;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.WorkflowExecutionEntity;
import com.alinesno.infra.smart.assistant.role.utils.CodeBlockParser;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.assistant.service.IWorkflowExecutionService;
import com.alinesno.infra.smart.im.dto.ChatMessageDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.dto.MessageUpdateDto;
import com.alinesno.infra.smart.im.enums.TaskStatusEnums;
import com.alinesno.infra.smart.im.service.IMessageService;
import com.alinesno.infra.smart.im.service.ISSEService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * WorkflowServiceImpl
 */
@Slf4j
@Service
public class WorkflowServiceImpl implements IWorkflowService {

    @Autowired
    private IWorkflowExecutionService workflowExecutionService;

    @Autowired
    private IMessageService messageService ;

    @Autowired
    private ISSEService sseService;

    @Autowired
    private IIndustryRoleService roleService ;

    @SneakyThrows
    @Override
    public void updateContent(MessageUpdateDto dto) {

        WorkflowExecutionEntity workflowExecution = workflowExecutionService.getById(dto.getBusinessId());
        workflowExecution.setId(null); // 变成新的消息进行保存

        if(Boolean.parseBoolean(dto.getCode())){
            workflowExecution.setGenContent(formatCodeBlack(dto.getContent()));
        }else{
            workflowExecution.setGenContent(dto.getContent().toString());
        }

        workflowExecutionService.save(workflowExecution) ;

        IndustryRoleEntity role = roleService.getById(workflowExecution.getRoleId());

        // 添加到消息记录表中
        MessageTaskInfo taskInfo = new MessageTaskInfo();

        taskInfo.setRoleId(workflowExecution.getRoleId());
        taskInfo.setChannelId(workflowExecution.getChannelId());
        taskInfo.setUsageTime(workflowExecution.getUsageTimeSeconds());
        taskInfo.setRoleDto(role);

        log.info("任务处理完成: {}", taskInfo);

        ChatMessageDto queMessage = AgentUtils.genTaskStatusMessageDto(taskInfo , workflowExecution.getUsageTimeSeconds()) ;

        if(Boolean.parseBoolean(dto.getCode())){
            queMessage.setChatText(formatCodeBlack(dto.getContent()));
        }else{
            queMessage.setChatText(dto.getContent());
        }

        queMessage.setBusinessId(workflowExecution.getId());
        queMessage.setRoleId(taskInfo.getRoleId());
        queMessage.setAccountId(taskInfo.getAccountId());
        queMessage.setLoading(false);
        queMessage.setStatus(TaskStatusEnums.COMPLETED.getValue());

        sseService.send(String.valueOf(taskInfo.getChannelId()), queMessage);

        // 保存到消息记录表中
        messageService.saveChatMessage(queMessage , taskInfo.getChannelId());
    }

    /**
     * 处理格式化使得返回显示更加合理
     * @param content
     * @return
     */
    private String formatCodeBlack(Object content) {
        // 使用代码包围起来
        String codeBlock = JSONUtil.toJsonPrettyStr(content) ;
        return "```json\n" + codeBlock + "\n```";
    }

    @Override
    public WorkflowExecutionDto getWorkflowExecution(String businessId) {

        WorkflowExecutionEntity workflowExecutionEntity = workflowExecutionService.getById(businessId);

        WorkflowExecutionDto dto = new WorkflowExecutionDto() ;
        BeanUtils.copyProperties(workflowExecutionEntity, dto);

        // 执行任务并记录
        String gentContent = workflowExecutionEntity.getGenContent() ;
        List<CodeContent> codeContentList = CodeBlockParser.parseCodeBlocks(gentContent) ;

        // TODO 支持其它语言
        if(!codeContentList.isEmpty() && codeContentList.get(0).getLanguage().equals("json")){
            dto.setCodeContent(codeContentList);
            dto.setCoding(true);
        }else{
            dto.setCoding(false);
        }

        return dto ;
    }
}
