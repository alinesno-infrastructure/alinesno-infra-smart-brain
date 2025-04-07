package com.alinesno.infra.smart.assistant.scene.scene.documentReview.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.scene.core.entity.DocReviewSceneEntity;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.DocReviewInitDto;

/**
 *
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
public interface IDocReviewSceneService extends IBaseService<DocReviewSceneEntity> {

    /**
     * 初始化智能助手
     * @param dto
     */
    void initAgents(DocReviewInitDto dto);

    /**
     * 根据场景ID获取场景信息
     * @param sceneId
     * @return
     */
    DocReviewSceneEntity getBySceneId(long sceneId);
}
