package com.alinesno.infra.smart.assistant.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.entity.RolePluginEntity;

import java.util.List;

/**
 * 应用构建Service接口
 * 
 * @version 1.0.0
 * @author luoxiaodong
 */
public interface IRolePluginService extends IBaseService<RolePluginEntity> {

    /**
     * 查询用户插件
     */
    List<RolePluginEntity> findPlugins(Long roleId);

    /**
     * 更新用户插件
     */
    boolean updateRolePlugin(Long roleId, List<RolePluginEntity> pluginIds);

}