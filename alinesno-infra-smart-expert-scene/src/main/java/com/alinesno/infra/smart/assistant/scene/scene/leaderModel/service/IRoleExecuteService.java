package com.alinesno.infra.smart.assistant.scene.scene.leaderModel.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.scene.dto.RoleTaskDto;
import com.alinesno.infra.smart.scene.entity.RoleExecuteEntity;

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
     * @param sceneId
     * @param tasks
     */
    void saveNewTasks(long sceneId, List<RoleTaskDto> tasks);

    /**
     * 是否当前频道的任务全部完成
     * @param sceneId
     * @return
     */
    boolean isAllTaskCompleted(Long sceneId);

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
    String getPreContent(String preRoleId , long sceneId);
}
