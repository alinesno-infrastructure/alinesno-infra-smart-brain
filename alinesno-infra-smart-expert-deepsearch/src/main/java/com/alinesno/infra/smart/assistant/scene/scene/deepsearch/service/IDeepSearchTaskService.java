package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.scene.entity.DeepSearchTaskEntity;

public interface IDeepSearchTaskService extends IBaseService<DeepSearchTaskEntity> {

    /**
     * 更新任务名称和描述
     *
     * @param taskEntity
     * @param title
     * @param desc
     */
    void updateTitleAndDescById(DeepSearchTaskEntity taskEntity, String title, String desc);

}
