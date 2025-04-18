package com.alinesno.infra.smart.assistant.scene.scene.documentReview.service;


import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.DocReviewRuleGroupDto;
import com.alinesno.infra.smart.scene.entity.DocReviewRuleGroupEntity;

import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
public interface IDocReviewRuleGroupService extends IBaseService<DocReviewRuleGroupEntity> {

    /**
     * 查询当前所有规则组及当前规则组下的所有规则列表
     * @param query
     * @return
     */
    List<DocReviewRuleGroupDto> listGroup(PermissionQuery query);

    /**
     * 删除分组之后，当中关联的规则也会被删除掉
     * @param id
     */
    void removeGroup(Long id);

    /**
     * 导入数据
     * @param result
     * @param query
     */
    void importExcelData(Map<String, List<Map<String, Object>>> result, PermissionQuery query);
}
