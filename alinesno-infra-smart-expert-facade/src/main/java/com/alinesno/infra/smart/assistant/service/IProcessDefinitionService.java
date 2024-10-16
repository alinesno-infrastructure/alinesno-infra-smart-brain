package com.alinesno.infra.smart.assistant.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.api.task.ProcessDefinitionDto;
import com.alinesno.infra.smart.assistant.api.task.ProcessTaskValidateDto;
import com.alinesno.infra.smart.assistant.api.task.TaskInfoBean;
import com.alinesno.infra.smart.assistant.entity.ProcessDefinitionEntity;
import com.alinesno.infra.smart.assistant.entity.TaskDefinitionEntity;

import java.util.List;

public interface IProcessDefinitionService extends IBaseService<ProcessDefinitionEntity> {

    /**
     * 运行任务实例
     * @param task
     * @param taskDefinitionList
     */
    void runProcess(TaskInfoBean task, List<TaskDefinitionEntity> taskDefinitionList);

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

    /**
     * 查询最近count条流程定义
     * @param count
     * @return
     */
    List<ProcessDefinitionEntity> queryRecentlyProcess(int count);

    /**
     * 更新流程定义
     * @param dto
     */
    void updateProcessDefinition(ProcessDefinitionDto dto);

}
