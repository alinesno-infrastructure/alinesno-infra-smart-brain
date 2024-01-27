package com.alinesno.infra.smart.brain.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.brain.api.BrainTaskDto;
import com.alinesno.infra.smart.brain.api.reponse.TaskContentDto;
import com.alinesno.infra.smart.brain.entity.GenerateTaskEntity;

import java.util.List;

public interface IGenerateTaskService extends IBaseService<GenerateTaskEntity> {

    /**
     * 提交任务
     * @param dto
     */
    void commitTask(BrainTaskDto dto);

    /**
     * 提交任务到CMS服务中
     * @param dto
     */
    void commitTaskToCms(BrainTaskDto dto);

    /**
     * 查询所有未完成的任务
     * @return
     */
    List<GenerateTaskEntity> getAllUnfinishedTasks(int retryCount);

    /**
     * 获取到LLM生成的内容
     * @param businessId
     * @return
     */
    TaskContentDto getContentByBusinessId(String businessId);

    /**
     * 重置重试次数
     * @param taskId
     */
    void resetRetry(Long taskId);

    /**
     * 更新生成内容
     * @param dto
     */
    void modifyContent(TaskContentDto dto);

    /**
     * 查询出所有进行超时而且异常的任务
     * @param jobOutTime
     * @return
     */
    List<GenerateTaskEntity> getAllUnstableTasks(int jobOutTime);

    /**
     * 重新运行任务服务
     * @param dto
     */
    void resetRetryTask(GenerateTaskEntity dto);

}
