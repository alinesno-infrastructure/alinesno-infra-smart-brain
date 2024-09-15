package com.alinesno.infra.smart.assistant.role.agents.aipteam;

import com.alinesno.infra.smart.assistant.api.CodeContent;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.WorkflowExecutionEntity;
import com.alinesno.infra.smart.assistant.enums.AssistantConstants;
import com.alinesno.infra.smart.assistant.role.ExpertService;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service(AssistantConstants.PREFIX_ASSISTANT + "ensXkJnK")
public class AIPDbBackupExpert extends ExpertService {

    @Override
    protected String handleFunctionCall(IndustryRoleEntity role,
                                        WorkflowExecutionEntity workflowExecution,
                                        List<CodeContent> codeContentList,
                                        MessageTaskInfo taskInfo) {
        String link = "https://www.kdocs.cn/l/cqZbwSTUGZNz";
        String fileName = "数据库过程完成记录报告" ;

        return generatorFileResponse("docx", fileName , link) ;
    }

    @Override
    protected String handleRole(IndustryRoleEntity role, WorkflowExecutionEntity workflowExecution, MessageTaskInfo taskInfo) {
        return "数据备份完成." ;
    }


}
