package com.alinesno.infra.smart.assistant.role;

import com.alinesno.infra.smart.assistant.api.CodeContent;
import com.alinesno.infra.smart.assistant.api.prompt.PromptMessage;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.WorkflowExecutionEntity;
import com.alinesno.infra.smart.assistant.enums.AssistantConstants;
import com.alinesno.infra.smart.brain.api.dto.PromptMessageDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 默认专家，如果没有其它任何专家的情况下，会默认使用这个
 */
@Slf4j
@Service(AssistantConstants.PREFIX_ASSISTANT_DEFAULT)
public class DefaultExpert extends ExpertService {

    @Override
    public String handleRole(IndustryRoleEntity role,
                             WorkflowExecutionEntity workflowExecutionEntity,
                             MessageTaskInfo taskInfo) {

        String message = clearMessage(taskInfo.getText()) ;
        String promptContent = role.getPromptContent() ;

        List<PromptMessageDto> messageEntityList = queryChannelLastMessage(taskInfo) ;
        messageEntityList.forEach(item -> log.debug("item:{}", item));

        List<PromptMessage> promptMessages = parseMessage(promptContent , message) ;
        String gentContent = qianWenLLM.processFile(promptMessages) ;

        log.debug("result:{}", gentContent);

        return gentContent ;
    }

    /**
     * 生成调用方法函数
     * @param role
     * @param workflowExecutionEntity
     * @param codeContentList
     * @param taskInfo
     * @return
     */
    @Override
    protected String handleFunctionCall(IndustryRoleEntity role,
                                        WorkflowExecutionEntity workflowExecutionEntity,
                                        List<CodeContent> codeContentList,
                                        MessageTaskInfo taskInfo) {

        log.debug("handleFunctionCall:{}", taskInfo);

        return "任务处理完成.";
    }

}

