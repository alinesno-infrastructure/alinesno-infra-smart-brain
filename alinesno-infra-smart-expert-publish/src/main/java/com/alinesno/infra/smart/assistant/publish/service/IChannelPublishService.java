package com.alinesno.infra.smart.assistant.publish.service;

import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.publish.entity.ChannelPublishEntity;

import java.util.List;
import java.util.Map;

/**
 * 发布渠道表Service接口
 */
public interface IChannelPublishService extends IBaseService<ChannelPublishEntity> {

    /**
     * 更新渠道配置
     * @param entity
     */
    void updateConfig(ChannelPublishEntity entity);

    /**
     * 根据 paramKey 列表查询配置信息
     *
     * @param paramKeys paramKey 列表
     * @param query
     * @param roleId
     * @return 配置信息映射，键为 paramKey，值为对应的配置信息
     */
    Map<String, ChannelPublishEntity> getConfigsByParamKeys(List<String> paramKeys, PermissionQuery query, long roleId);

    /**
     * 根据分享Token
     * @param shareId
     * @return
     */
    ChannelPublishEntity getByShareToken(String shareId);

}
