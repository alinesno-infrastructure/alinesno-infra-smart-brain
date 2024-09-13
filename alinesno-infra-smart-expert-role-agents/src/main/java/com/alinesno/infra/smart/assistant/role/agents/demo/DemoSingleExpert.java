package com.alinesno.infra.smart.assistant.role.agents.demo;

import com.alinesno.infra.common.core.utils.DateUtils;
import com.alinesno.infra.smart.assistant.api.CodeContent;
import com.alinesno.infra.smart.assistant.api.prompt.PromptMessage;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.WorkflowExecutionEntity;
import com.alinesno.infra.smart.assistant.enums.AssistantConstants;
import com.alinesno.infra.smart.assistant.role.ExpertService;
import com.alinesno.infra.smart.brain.api.dto.PromptMessageDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 单个专家
 */
@Slf4j
@Service(AssistantConstants.PREFIX_ASSISTANT + "MNigoYFn")
public class DemoSingleExpert extends ExpertService {

    @Override
    public String handleRole(IndustryRoleEntity role,
                             WorkflowExecutionEntity workflowExecutionEntity,
                             MessageTaskInfo taskInfo) {

        String message = taskInfo.getText() ;
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

        String fileName = role.getRoleName() + DateUtils.dateTimeNow();
        String fileType = "word" ;

        return generatorFileResponse(fileType, fileName , null);
    }

}
