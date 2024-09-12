package com.alinesno.infra.smart.assistant.chain;

import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.WorkflowExecutionEntity;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;

// 创建 Expert 接口
public interface IBaseExpertService {

    /**
     * 专家运行
     *
     * @param role                    角色信息
     * @param workflowExecutionEntity 工作流执行实体
     * @param taskInfo                消息任务信息
     * @return
     */
    WorkflowExecutionDto runRoleAgent(IndustryRoleEntity role, WorkflowExecutionEntity workflowExecutionEntity, MessageTaskInfo taskInfo);

}