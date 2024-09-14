package com.alinesno.infra.base.im.service;

import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.im.dto.MessageUpdateDto;

/**
 * 服务任务执行
 */
public interface IWorkflowService {

    void updateContent(MessageUpdateDto dto);

    WorkflowExecutionDto getWorkflowExecution(String businessId);
}
