package com.alinesno.infra.smart.assistant.plugin.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.entity.RoleToolEntity;
import com.alinesno.infra.smart.assistant.plugin.mapper.RoleToolMapper;
import com.alinesno.infra.smart.assistant.service.IRoleToolService;
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
public class RoleToolServiceImpl extends IBaseServiceImpl<RoleToolEntity, RoleToolMapper> implements IRoleToolService {

    @Override
    public List<RoleToolEntity> findTools(Long roleId) {
        return null;
    }

    @Override
    public boolean updateRoleTools(Long roleId, List<RoleToolEntity> tools) {
        return false;
    }

}