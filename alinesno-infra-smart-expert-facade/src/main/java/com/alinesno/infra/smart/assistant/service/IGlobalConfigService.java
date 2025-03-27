package com.alinesno.infra.smart.assistant.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.entity.GlobalConfigEntity;

/**
 * 应用构建Service接口
 * 
 * @version 1.0.0
 * @author luoxiaodong
 */
public interface IGlobalConfigService extends IBaseService<GlobalConfigEntity> {

    /**
     * 根据组织ID获取全局配置信息
     * @param orgId
     * @return
     */
    GlobalConfigEntity getByOrganizationId(Long orgId);
}