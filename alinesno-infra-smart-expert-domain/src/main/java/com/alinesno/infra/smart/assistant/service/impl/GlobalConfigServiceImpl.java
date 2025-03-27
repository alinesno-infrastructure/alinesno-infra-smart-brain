package com.alinesno.infra.smart.assistant.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.entity.GlobalConfigEntity;
import com.alinesno.infra.smart.assistant.mapper.GlobalConfigMapper;
import com.alinesno.infra.smart.assistant.service.IGlobalConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 应用构建Service业务层处理
 * 
 * @version 1.0.0
 * @author luoxiaodong
 */
@Slf4j
@Service
public class GlobalConfigServiceImpl extends IBaseServiceImpl<GlobalConfigEntity, GlobalConfigMapper> implements IGlobalConfigService {

    @Override
    public GlobalConfigEntity getByOrganizationId(Long orgId) {
        LambdaQueryWrapper<GlobalConfigEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GlobalConfigEntity::getOrgId, orgId);
        if(count(wrapper) > 0){
            return getOne(wrapper);
        }
        return null;
    }
}