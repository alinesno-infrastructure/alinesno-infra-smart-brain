package com.alinesno.infra.smart.assistant.role.agents.demo;

import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.WorkflowExecutionEntity;
import com.alinesno.infra.smart.assistant.enums.AssistantConstants;
import com.alinesno.infra.smart.assistant.role.ExpertService;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 多个专家示例
 */
@Slf4j
@Service(AssistantConstants.PREFIX_ASSISTANT + "demoMultipleExpert")
public class DemoMultipleExpert extends ExpertService {

    @Override
    protected String handleRole(IndustryRoleEntity role,
                                WorkflowExecutionEntity workflowExecutionEntity,
                                MessageTaskInfo taskInfo) {
        return null;
    }

}
