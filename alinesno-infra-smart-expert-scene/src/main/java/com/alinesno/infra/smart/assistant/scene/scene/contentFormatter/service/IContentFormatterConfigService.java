package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service;

import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto.ContentFormatterRewriteConfigDto;
import com.alinesno.infra.smart.scene.entity.ContentFormatterConfigEntity;

public interface IContentFormatterConfigService extends IBaseService<ContentFormatterConfigEntity> {

    /**
     * 更新重写配置
     */
    void updateRewriteConfig(ContentFormatterRewriteConfigDto dto);

    /**
     * 初始化配置
     * @param query
     */
    void initConfig(PermissionQuery query);
}