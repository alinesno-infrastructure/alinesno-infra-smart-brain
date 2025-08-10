package com.alinesno.infra.smart.assistant.scene.scene.documentReview.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.GenAuditResultDto;
import com.alinesno.infra.smart.scene.entity.DocReviewAuditResultEntity;

import java.util.List;

/**
 *
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
public interface IDocReviewAuditResultService extends IBaseService<DocReviewAuditResultEntity> {

    /**
     * 保存审核结果，会很清理当前sceneId下面的旧数据
     *
     * @param auditResultList
     * @param dto
     */
    void saveAuditResult(List<DocReviewAuditResultEntity> auditResultList, GenAuditResultDto dto);

    /**
     * 根据规则ID和场景ID获取审核结果
     *
     * @param ruleId
     * @param sceneId
     * @param taskId
     * @return
     */
    List<DocReviewAuditResultEntity> getAuditResultByRuleId(long ruleId, long sceneId, long taskId);

    /**
     * 通过场景id标识获取到所有的审核结果
     *
     * @param sceneId
     * @param docReviewSceneId
     * @param taskId
     * @return
     */
    List<DocReviewAuditResultEntity> getBySceneIdAndDocReviewSceneId(Long sceneId , Long docReviewSceneId, long taskId) ;
}
