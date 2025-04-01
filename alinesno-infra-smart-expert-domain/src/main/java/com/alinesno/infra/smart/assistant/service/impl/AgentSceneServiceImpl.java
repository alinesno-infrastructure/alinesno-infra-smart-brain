package com.alinesno.infra.smart.assistant.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.mapper.AgentSceneMapper;
import com.alinesno.infra.smart.im.entity.AgentSceneEntity;
import com.alinesno.infra.smart.im.service.IAgentSceneService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AgentSceneServiceImpl extends IBaseServiceImpl<AgentSceneEntity, AgentSceneMapper> implements IAgentSceneService {

    @Override
    public void addRoleToScene(Long roleId, long sceneId, long sceneAgentId) {
        // 判断角色是否已经在场景里面
        if(roleId != 0){
            AgentSceneEntity agentSceneEntity = new AgentSceneEntity();
            agentSceneEntity.setAgentId(roleId);
        }
    }
}
