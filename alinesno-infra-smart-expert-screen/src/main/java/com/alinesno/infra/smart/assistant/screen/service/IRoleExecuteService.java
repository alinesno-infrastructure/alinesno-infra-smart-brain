package com.alinesno.infra.smart.assistant.screen.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.screen.dto.RoleTaskDto;
import com.alinesno.infra.smart.assistant.screen.entity.RoleExecuteEntity;

import java.util.List;

/**
 *
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
public interface IRoleExecuteService extends IBaseService<RoleExecuteEntity> {

    /**
     * 更新任务状态
     *
     * @param screenId
     * @param tasks
     */
    void saveNewTasks(long screenId, List<RoleTaskDto> tasks);

    /**
     * 是否当前频道的任务全部完成
     * @param screenId
     * @return
     */
    boolean isAllTaskCompleted(Long screenId);

    /**
     * 标识任务已经完成
     * @param id
     */
    void finishTask(Long id);

    /**
     * 获取前一步任务的内容
     * @param preRoleId
     * @return
     */
    String getPreContent(String preRoleId , long screenId);
}
