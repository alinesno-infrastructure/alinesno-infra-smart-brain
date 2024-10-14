package com.alinesno.infra.smart.assistant.role.agents.demo;

import com.alinesno.infra.common.core.utils.DateUtils;
import com.alinesno.infra.smart.assistant.api.CodeContent;
import com.alinesno.infra.smart.assistant.api.prompt.PromptMessage;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.WorkflowExecutionEntity;
import com.alinesno.infra.smart.assistant.enums.AssistantConstants;
import com.alinesno.infra.smart.assistant.role.ExpertService;
import com.alinesno.infra.smart.assistant.role.llm.adapter.MessageManager;
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

    /**
     * 发送模板消息
     */
    @Override
    public String handleRole(IndustryRoleEntity role,
                             WorkflowExecutionEntity workflowExecution,
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
     * 内容评审修改
     */
    @Override
    protected String handleModifyCall(IndustryRoleEntity role,
                                        WorkflowExecutionEntity workflowExecution,
                                        List<CodeContent> codeContentList,
                                        MessageTaskInfo taskInfo) {

        String chatText = clearMessage(taskInfo.getText()) ;
        String preContent = workflowExecution.getGenContent() ;

        // TODO 添加system prompt来进行条件的限制
        String newPromptContent = "你现在根据我的反馈修改建议进行调整修改，但是不要改变原来的格式。\r\n" +
                "原来的内容:\r\n" +
                preContent + "\r\n" +
                "我的修改意见:" + chatText + "\r\n" +
                "修改后的内容:\r\n" +
                "<在这里输出修改后的内容>\r\n" +
                "注意:\r\n" +
                "1. 原来的内容可以不用返回\r\n" +
                "2. 如果包含json，在原基础格式上进行修改，不要改变原来的json格式";

        MessageManager msgManager = new MessageManager(10);
        msgManager.add(ofUser(newPromptContent)) ;
        StringBuilder gentContent = qianWenLLM.chatComponent(msgManager) ;

        log.debug("result:{}", gentContent.toString());

        return gentContent.toString() ;
    }


    /**
     * 生成调用方法函数
     */
    @Override
    protected String handleFunctionCall(IndustryRoleEntity role,
                                        WorkflowExecutionEntity workflowExecution,
                                        List<CodeContent> codeContentList,
                                        MessageTaskInfo taskInfo) {

        log.debug("handleFunctionCall:{}", taskInfo);

        String fileName = role.getRoleName() + DateUtils.dateTimeNow();
        String fileType = "word" ;

        return generatorFileResponse(fileType, fileName , null);
    }

}
