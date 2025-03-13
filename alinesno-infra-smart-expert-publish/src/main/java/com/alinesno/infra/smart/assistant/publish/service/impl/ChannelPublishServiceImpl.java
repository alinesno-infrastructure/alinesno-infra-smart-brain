package com.alinesno.infra.smart.assistant.publish.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.smart.assistant.publish.entity.ChannelPublishEntity;
import com.alinesno.infra.smart.assistant.publish.mapper.ChannelPublishMapper;
import com.alinesno.infra.smart.assistant.publish.service.IChannelPublishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 频道发布服务实现类
 */
@Slf4j
@Service
public class ChannelPublishServiceImpl extends IBaseServiceImpl<ChannelPublishEntity, ChannelPublishMapper> implements IChannelPublishService {

    @Override
    public void updateConfig(ChannelPublishEntity entity) {
        this.saveOrUpdate(entity);
    }

    @Override
    public Map<String, ChannelPublishEntity> getConfigsByParamKeys(List<String> paramKeys, PermissionQuery query, long roleId) {

        LambdaQueryWrapper<ChannelPublishEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.setEntityClass(ChannelPublishEntity.class);
        query.toWrapper(wrapper);
        wrapper.in(ChannelPublishEntity::getParamKey, paramKeys);
        wrapper.eq(ChannelPublishEntity::getRoleId, roleId);

        List<ChannelPublishEntity> entities = list(wrapper) ;
        Map<String, ChannelPublishEntity> configMap = new HashMap<>();
        for (ChannelPublishEntity entity : entities) {
            configMap.put(entity.getParamKey(), entity);
        }

        return configMap;
    }

    @Override
    public ChannelPublishEntity getByShareToken(String shareId) {

        if(shareId != null){
            LambdaQueryWrapper<ChannelPublishEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.setEntityClass(ChannelPublishEntity.class);
            wrapper.eq(ChannelPublishEntity::getApiKey, shareId);
            return getOne(wrapper);
        }

        return null;
    }

}