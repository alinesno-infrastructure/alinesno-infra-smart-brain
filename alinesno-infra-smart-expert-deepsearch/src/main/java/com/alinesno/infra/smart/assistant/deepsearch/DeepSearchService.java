package com.alinesno.infra.smart.assistant.deepsearch;

import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.enums.AssistantConstants;
import com.alinesno.infra.smart.assistant.role.ExpertService;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.entity.MessageEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * 深度搜索服务
 */
@Scope("prototype")
@Slf4j
@Service(AssistantConstants.PREFIX_ASSISTANT_DEEP_SEARCH)
public class DeepSearchService extends ExpertService {

    @Override
    protected String handleRole(IndustryRoleEntity role, MessageEntity workflowExecution, MessageTaskInfo taskInfo) {
        return null;
    }
}
