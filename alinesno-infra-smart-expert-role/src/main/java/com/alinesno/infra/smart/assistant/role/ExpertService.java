package com.alinesno.infra.smart.assistant.role;

import com.alibaba.fastjson.JSONArray;
import com.alinesno.infra.smart.assistant.api.CodeContent;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.api.prompt.PromptMessage;
import com.alinesno.infra.smart.assistant.chain.IBaseExpertService;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.WorkflowExecutionEntity;
import com.alinesno.infra.smart.assistant.enums.WorkflowStatusEnum;
import com.alinesno.infra.smart.assistant.role.llm.QianWenLLM;
import com.alinesno.infra.smart.assistant.role.utils.CodeBlockParser;
import com.alinesno.infra.smart.assistant.role.utils.RoleUtils;
import com.alinesno.infra.smart.assistant.role.utils.TemplateParser;
import com.alinesno.infra.smart.assistant.service.IWorkflowExecutionService;
import com.alinesno.infra.smart.brain.api.dto.PromptMessageDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.service.IMessageService;
import com.plexpt.chatgpt.entity.chat.Message;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 创建父类 ITExpert 并声明为抽象类
@Slf4j
@Data
public abstract class ExpertService implements IBaseExpertService {

    @Autowired
    protected QianWenLLM qianWenLLM;

    @Autowired
    private IMessageService messageService;

    @Autowired
    private IWorkflowExecutionService workflowExecutionService ;

    @Override
    public WorkflowExecutionDto runRoleAgent(IndustryRoleEntity role, WorkflowExecutionEntity workflowExecutionEntity, MessageTaskInfo taskInfo) {

        // 任务开始记录
        WorkflowExecutionEntity record = new WorkflowExecutionEntity() ;
        record.setRoleId(role.getId());
        record.setBuildNumber(1) ;
        record.setStartTime(System.currentTimeMillis());
        record.setStatus(WorkflowStatusEnum.IN_PROGRESS.getStatus());
        workflowExecutionService.save(record);

        // 处理业务
        String gentContent = handleRole(role , workflowExecutionEntity , taskInfo);

        // 处理完成之后记录更新
        record.setStatus(WorkflowStatusEnum.COMPLETED.getStatus());
        record.setEndTime(System.currentTimeMillis());
        record.setUsageTimeSeconds(RoleUtils.formatTime(record.getStartTime(), record.getEndTime()));

        // 解析出生成的内容
        record.setGenContent(gentContent);

        List<CodeContent> codeContentList = CodeBlockParser.parseCodeBlocks(gentContent) ;

        workflowExecutionService.update(record);

        WorkflowExecutionDto recordDto = new WorkflowExecutionDto() ;
        BeanUtils.copyProperties(record, recordDto);

        recordDto.setCodeContent(codeContentList);

        return recordDto;
    }

    /**
     * 处理业务流
     * @param role
     * @param workflowExecutionEntity
     * @param taskInfo
     */
    protected abstract String handleRole(IndustryRoleEntity role, WorkflowExecutionEntity workflowExecutionEntity, MessageTaskInfo taskInfo) ;

    protected List<PromptMessageDto> queryChannelLastMessage(MessageTaskInfo taskInfo) {

        long channel = taskInfo.getChannelId() ;
        long accountId = taskInfo.getAccountId() ;
        long roleId = taskInfo.getRoleId() ;
        int siz = 10 ;

        return messageService.queryChannelLastMessage(channel , accountId , roleId , siz);
    }

    protected List<PromptMessage> parseMessage(String promptContent , String params) {
        List<PromptMessageDto> promptMessageList = JSONArray.parseArray(promptContent , PromptMessageDto.class) ;

        List<PromptMessage> messages = new ArrayList<>();

        for(PromptMessageDto msg : promptMessageList){
            PromptMessage message = null ;
            // 模板解析处理
            String contentTemplate = msg.getContent().trim() ;

            Map<String, Object> paramMap = new HashMap<>() ;

            paramMap.put("label1" , params);

            if(params != null){
                contentTemplate = TemplateParser.parserTemplate(contentTemplate , paramMap) ;
            }
            if(Message.Role.SYSTEM.getValue().equals(msg.getRole())){
                message = PromptMessage.ofSystem(contentTemplate);
            }else if(Message.Role.ASSISTANT.getValue().equals(msg.getRole())){
                message = PromptMessage.ofAssistant(contentTemplate);
            }else if(Message.Role.USER.getValue().equals(msg.getRole())){
                message = PromptMessage.of(contentTemplate);
            }

            if(message != null){
                messages.add(message) ;
            }
        }

        return messages;
    }
}
