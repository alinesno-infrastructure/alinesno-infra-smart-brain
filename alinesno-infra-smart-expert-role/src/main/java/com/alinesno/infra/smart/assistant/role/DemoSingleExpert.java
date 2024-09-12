package com.alinesno.infra.smart.assistant.role;

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
 * 单个专家
 */
@Slf4j
@Service(AssistantConstants.PREFIX_ASSISTANT + "demoSingleExpert")
public class DemoSingleExpert extends ExpertService {

    @Override
    public String handleRole(IndustryRoleEntity role, WorkflowExecutionEntity workflowExecutionEntity, MessageTaskInfo taskInfo) {

        String message = taskInfo.getText() ;
        String promptContent = role.getPromptContent() ;

        List<PromptMessageDto> messageEntityList = queryChannelLastMessage(taskInfo) ;
        messageEntityList.forEach(item -> log.debug("item:{}", item));

        List<PromptMessage> promptMessages = parseMessage(promptContent , message) ;
        String gentContent = qianWenLLM.processFile(promptMessages) ;

        log.debug("result:{}", gentContent);

        return gentContent ;
    }



}
