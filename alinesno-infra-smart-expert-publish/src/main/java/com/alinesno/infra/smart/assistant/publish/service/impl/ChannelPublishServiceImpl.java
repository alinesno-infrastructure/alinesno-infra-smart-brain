package com.alinesno.infra.smart.assistant.publish.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.smart.assistant.publish.dto.ChannelPublishDTO;
import com.alinesno.infra.smart.assistant.publish.entity.ChannelPublishEntity;
import com.alinesno.infra.smart.assistant.publish.enums.ChannelListEnums;
import com.alinesno.infra.smart.assistant.publish.mapper.ChannelPublishMapper;
import com.alinesno.infra.smart.assistant.publish.service.IChannelPublishService;
import com.alinesno.infra.smart.im.service.IAgentSceneService;
import com.alinesno.infra.smart.im.service.IAgentStoreService;
import com.alinesno.infra.smart.scene.enums.SceneScopeType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private IAgentStoreService agentStoreService ;

    @Autowired
    private IAgentSceneService agentSceneService ;

    @Override
    public void updateConfig(ChannelPublishEntity entity, ChannelPublishDTO dto) {
        this.saveOrUpdate(entity);

        // 如果是发布到商店，则将角色信息添加到AIP商店里面
        if(ChannelListEnums.AIP_AGENT_STORE.getParamKey().equals(entity.getParamKey())){
            agentStoreService.addRoleToStore(entity.getRoleId() ,
                    dto.getAgentStoreType() ,
                    SceneScopeType.PUBLIC_SCENE.getValue() ,  // 商店是公开的角色
                    dto.getOrgId()) ;
        }

        // 如果是发布到场景，则将角色信息添加到AIP场景里面
        if(ChannelListEnums.AIP_AGENT_SCENE.getParamKey().equals(entity.getParamKey())){
            agentSceneService.addRoleToScene(entity.getRoleId() ,
                    dto.getSceneId() ,
                    dto.getAgentTypeId() ,
                    dto.getLlmModelId() ,
                    dto.getImageModelId() ,
                    dto.getSceneScope() ,
                    dto.getOrgId()) ;
        }
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

    @Override
    public void offlineChannel(long channelId) {
        ChannelPublishEntity entity = this.getById(channelId) ;
        this.removeById(channelId);

        // 如果是发布到商店，则将角色信息添加到AIP商店里面
        if(ChannelListEnums.AIP_AGENT_STORE.getParamKey().equals(entity.getParamKey())){
            agentStoreService.offlineStore(entity.getRoleId()) ;
        }

        // 如果是发布到场景，则将角色信息添加到AIP场景里面
        if(ChannelListEnums.AIP_AGENT_SCENE.getParamKey().equals(entity.getParamKey())){
            agentSceneService.offlineScene(entity.getRoleId()) ;
        }
    }

}