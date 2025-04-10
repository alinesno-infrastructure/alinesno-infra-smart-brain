package com.alinesno.infra.smart.assistant.scene.scene.documentReview.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.DocReviewInitDto;
import com.alinesno.infra.smart.scene.dto.UpdateSceneAgentDto;
import com.alinesno.infra.smart.scene.entity.DocReviewSceneEntity;

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

    /**
     * 更新场景的智能助手
     * @param dto
     */
    void updateSceneAgents(UpdateSceneAgentDto dto);
}
