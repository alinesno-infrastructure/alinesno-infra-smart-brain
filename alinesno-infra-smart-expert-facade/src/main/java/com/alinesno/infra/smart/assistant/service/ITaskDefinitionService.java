package com.alinesno.infra.smart.assistant.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.api.task.ProcessDefinitionDto;
import com.alinesno.infra.smart.assistant.api.task.ProcessTaskValidateDto;
import com.alinesno.infra.smart.assistant.entity.TaskDefinitionEntity;

public interface ITaskDefinitionService extends IBaseService<TaskDefinitionEntity> {

    /**
     * 保存流程定义
     *
     * @param dto
     * @return
     */
    long commitProcessDefinition(ProcessDefinitionDto dto);

    /**
     * 运行验证任务
     * @param dto
     */
    void runProcessTask(ProcessTaskValidateDto dto);

//    /**
//     * 更新流程定义
//     * @param dto
//     */
//    void updateProcessDefinition(ProcessDefinitionDto dto);

    /**
     * 验证流程
     * @param dto
     * @return
     */
    long validateProcessDefinition(ProcessDefinitionDto dto);

}
