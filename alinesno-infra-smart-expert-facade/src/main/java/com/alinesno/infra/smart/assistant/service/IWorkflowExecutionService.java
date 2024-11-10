package com.alinesno.infra.smart.assistant.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.entity.WorkflowExecutionEntity;

/**
 * 应用构建Service接口
 *
 * @version 1.0.0
 * @author luoxiaodong
 */
public interface IWorkflowExecutionService extends IBaseService<WorkflowExecutionEntity> {

    /**
     * 获取名称获取到构建次数
     * @param chainName
     * @return
     */
    int getByChainName(String chainName);

    /**
     * 保存记录
     * @param record
     */
    void saveRecord(WorkflowExecutionDto record);

//    /**
//     * @param taskInfo
//     * @param genContent
//     * @param messageId
//     */
//    void saveWorkflowExecution(MessageTaskInfo taskInfo, WorkflowExecutionDto genContent, long messageId);
}