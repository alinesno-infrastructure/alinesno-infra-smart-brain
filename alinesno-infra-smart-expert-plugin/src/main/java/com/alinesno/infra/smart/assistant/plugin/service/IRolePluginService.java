package com.alinesno.infra.smart.assistant.plugin.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.entity.RolePluginEntity;

public interface IRolePluginService extends IBaseService<RolePluginEntity> {

    /**
     * 安装用户插件
     * @param accountId
     * @param pluginId
     */
    void installPlugin(long accountId, long pluginId);

    /**
     * 重新加载插件
     */
    void reloadPlugin();

}
