package com.alinesno.infra.smart.assistant.scene.scene.longtext.service;

import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.dto.TaskProgressDto;
import com.alinesno.infra.smart.scene.entity.LongTextTaskEntity;

import java.util.List;

/**
 * 描述： 通用助手任务
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
public interface ILongTextTaskService extends IBaseService<LongTextTaskEntity> {


    /**
     * 分页查询
     * @param page
     * @param query
     * @return
     */
    List<LongTextTaskEntity> pagerListByPage(DatatablesPageBean page, PermissionQuery query , Long sceneId);

    /**
     * 获取任务进度
     * @param taskId
     * @return
     */
    TaskProgressDto getProgress(Long taskId);

    /**
     * 手工接管任务，即将任务全部设置成完成的状态，由人工处理
     * @param taskId
     */
    void takeOver(Long taskId);
}
