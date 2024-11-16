package com.alinesno.infra.smart.assistant.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.entity.RolePluginEntity;
import com.alinesno.infra.smart.assistant.mapper.RolePluginMapper;
import com.alinesno.infra.smart.assistant.service.IRolePluginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 应用构建Service业务层处理
 * 
 * @version 1.0.0
 * @author luoxiaodong
 */
@Slf4j
@Service
public class RolePluginServiceImpl extends IBaseServiceImpl<RolePluginEntity, RolePluginMapper> implements IRolePluginService {

    @Override
    public List<RolePluginEntity> findPlugins(Long roleId) {
        return null;
    }

    @Override
    public boolean updateRolePlugin(Long roleId, List<RolePluginEntity> pluginIds) {
        return false;
    }

}