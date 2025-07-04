package com.alinesno.infra.smart.assistant.scene.scene.documentReview.service;

import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.scene.entity.DocReviewTaskEntity;
import com.alinesno.infra.smart.scene.entity.GeneralAgentTaskEntity;

import java.util.List;

/**
 * 描述： 通用助手任务
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
public interface IDocReviewTaskService extends IBaseService<DocReviewTaskEntity> {


    /**
     * 分页查询
     * @param page
     * @param query
     * @return
     */
    List<DocReviewTaskEntity> pagerListByPage(DatatablesPageBean page, PermissionQuery query , Long sceneId);

}
