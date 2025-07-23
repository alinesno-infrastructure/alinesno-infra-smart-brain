package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.scene.entity.ContentFormatterOfficeConfigEntity;

/**
 * Office工具配置服务接口
 */
public interface IContentFormatterOfficeConfigService extends IBaseService<ContentFormatterOfficeConfigEntity> {

    /**
     * 获取当前配置
     */
    ContentFormatterOfficeConfigEntity getConfig(Long orgId) ;

}