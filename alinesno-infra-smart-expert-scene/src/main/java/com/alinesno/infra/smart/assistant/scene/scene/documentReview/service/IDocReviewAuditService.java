package com.alinesno.infra.smart.assistant.scene.scene.documentReview.service;

import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.DocReviewAuditDto;
import com.alinesno.infra.smart.scene.entity.DocReviewAuditEntity;
import com.alinesno.infra.smart.scene.entity.DocReviewRulesEntity;

import java.util.List;

/**
 *
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
public interface IDocReviewAuditService extends IBaseService<DocReviewAuditEntity> {

    /**
     * 查询审核清单
     * @param id
     * @return
     */
    DocReviewAuditDto queryAudit(Long id);

    /**
     * 保存或更新审核清单
     * @param dto
     */
    Long saveOrUpdateAudit(DocReviewAuditDto dto);

    /**
     * 查询审核清单
     * @param query
     * @return
     */
    List<DocReviewAuditDto> getAuditList(PermissionQuery query);

    /**
     * 根据审核清单ID查询规则
     * @param auditId
     * @return
     */
    List<DocReviewRulesEntity> getRulesByAuditId(long auditId);

}
