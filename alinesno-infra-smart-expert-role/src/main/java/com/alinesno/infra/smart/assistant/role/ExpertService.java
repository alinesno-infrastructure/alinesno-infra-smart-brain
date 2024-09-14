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
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 创建父类 ITExpert 并声明为抽象类
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public abstract class ExpertService extends ExpertToolsService implements IBaseExpertService {

    @Autowired
    protected QianWenLLM qianWenLLM;

    @Autowired
    private IMessageService messageService;

    @Autowired
    private IWorkflowExecutionService workflowExecutionService ;

    /**
     * 执行角色
     * @param role                    角色信息
     * @param workflowExecutionEntity 工作流执行实体
     * @param taskInfo                消息任务信息
     * @return
     */
    @Override
    public WorkflowExecutionDto runRoleAgent(IndustryRoleEntity role, WorkflowExecutionEntity workflowExecutionEntity, MessageTaskInfo taskInfo) {

        WorkflowExecutionDto recordDto = new WorkflowExecutionDto() ;

        // 任务开始记录
        WorkflowExecutionEntity record = new WorkflowExecutionEntity() ;
        record.setRoleId(role.getId());
        record.setChannelId(taskInfo.getChannelId());
        record.setBuildNumber(1) ;
        record.setStartTime(System.currentTimeMillis());
        record.setStatus(WorkflowStatusEnum.IN_PROGRESS.getStatus());
        workflowExecutionService.save(record);

        if(taskInfo.isFunctionCall()){  // 执行方法

            String result = null ;
            if(workflowExecutionEntity == null){
               result = "请选择操作业务." ;
            }else{
                // 执行任务并记录
                String gentContent = workflowExecutionEntity.getGenContent() ;
                List<CodeContent> codeContentList = CodeBlockParser.parseCodeBlocks(gentContent) ;

                result = handleFunctionCall(role , workflowExecutionEntity , codeContentList , taskInfo);
            }

            BeanUtils.copyProperties(record, recordDto);
            recordDto.setGenContent(result);

        }else{
            // 处理业务
            String gentContent = handleRole(role , workflowExecutionEntity , taskInfo);

            // 解析出生成的内容
            record.setGenContent(gentContent);
            List<CodeContent> codeContentList = CodeBlockParser.parseCodeBlocks(gentContent) ;

            BeanUtils.copyProperties(record, recordDto);
            recordDto.setCodeContent(codeContentList);
        }

        // 处理完成之后记录更新
        record.setStatus(WorkflowStatusEnum.COMPLETED.getStatus());
        record.setEndTime(System.currentTimeMillis());
        record.setUsageTimeSeconds(RoleUtils.formatTime(record.getStartTime(), record.getEndTime()));

        workflowExecutionService.update(record);

        return recordDto;
    }

    /**
     * 处理函数调用
     *
     * @param role
     * @param workflowExecutionEntity
     * @param codeContentList
     * @param taskInfo
     * @return
     */
    protected String handleFunctionCall(IndustryRoleEntity role, WorkflowExecutionEntity workflowExecutionEntity, List<CodeContent> codeContentList, MessageTaskInfo taskInfo) {
        return null ;
    }

    /**
     * 处理业务流
     * @param role
     * @param workflowExecutionEntity
     * @param taskInfo
     */
    protected abstract String handleRole(IndustryRoleEntity role, WorkflowExecutionEntity workflowExecutionEntity, MessageTaskInfo taskInfo) ;

    /**
     * 查询历史消息
     * @param taskInfo
     * @return
     */
    protected List<PromptMessageDto> queryChannelLastMessage(MessageTaskInfo taskInfo) {

        long channel = taskInfo.getChannelId() ;
        long accountId = taskInfo.getAccountId() ;
        long roleId = taskInfo.getRoleId() ;
        int siz = 10 ;

        return messageService.queryChannelLastMessage(channel , accountId , roleId , siz);
    }

    /**
     * 解析消息
     * @param promptContent
     * @param params
     * @return
     */
    protected List<PromptMessage> parseMessage(String promptContent , String params) {
        Map<String, Object> paramMap = new HashMap<>() ;
        paramMap.put("label1" , params);
        return getPromptMessages(promptContent, params, paramMap);
    }

    /**
     * 解析消息
     * @param promptContent
     * @param params
     * @return
     */
    protected List<PromptMessage> parseMessage(String promptContent , String params , String promptDoc) {
        Map<String, Object> paramMap = new HashMap<>() ;
        paramMap.put("label1" , params);
        paramMap.put("prompt_doc" , promptDoc);
        return getPromptMessages(promptContent, params, paramMap);
    }

    /**
     * 获取提示消息
     * @param promptContent
     * @param params
     * @param paramMap
     * @return
     */
    @NotNull
    private static List<PromptMessage> getPromptMessages(String promptContent, String params, Map<String, Object> paramMap) {
        List<PromptMessageDto> promptMessageList = JSONArray.parseArray(promptContent, PromptMessageDto.class) ;
        List<PromptMessage> messages = new ArrayList<>();

        for(PromptMessageDto msg : promptMessageList){
            PromptMessage message = null ;
            // 模板解析处理
            String contentTemplate = msg.getContent().trim() ;

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

    public static String clearMessage(String message) {
        String[] words = message.split(" ");

        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!word.startsWith("@") && !word.startsWith("#")) {
                result.append(word);
            }
        }

        return result.toString();
    }
}
