package com.alinesno.infra.smart.assistant.scene.scene.articleWriting.service;


import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.scene.entity.ArticleTemplateEntity;

import java.util.List;

/**
 *
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
public interface IArticleTemplateService extends IBaseService<ArticleTemplateEntity> {

    /**
     * 根据类型获取模板
     *
     * @param query
     * @param typeId
     * @return
     */
    List<ArticleTemplateEntity> getTemplateByType(PermissionQuery query, String typeId);
}
