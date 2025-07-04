package com.alinesno.infra.smart.assistant.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.mapper.AgentSceneMapper;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.constants.AgentConstants;
import com.alinesno.infra.smart.im.entity.AgentSceneEntity;
import com.alinesno.infra.smart.im.service.IAgentSceneService;
import com.alinesno.infra.smart.scene.dto.SceneAgent;
import com.alinesno.infra.smart.scene.dto.SceneInfoDto;
import com.alinesno.infra.smart.scene.enums.SceneEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class AgentSceneServiceImpl extends IBaseServiceImpl<AgentSceneEntity, AgentSceneMapper> implements IAgentSceneService {

    @Autowired
    private IIndustryRoleService industryRoleService ;

    @Override
    public void addRoleToScene(Long roleId, long sceneId, long agentTypeId, long llmModelId, long imageModelId , String sceneScope, Long orgId) {

        log.debug("添加角色到场景中 roleId = {} , sceneId = {} ,agentTypeId = {} " , roleId , sceneId ,  agentTypeId);

        // 判断角色是否已经在场景里面
        if(roleId != 0){

            LambdaQueryWrapper<AgentSceneEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AgentSceneEntity::getAgentId , roleId) ;
            long count = count(wrapper) ;

            AgentSceneEntity agentSceneEntity = new AgentSceneEntity();

            if(count > 0){
                agentSceneEntity = getOne(wrapper) ;
            }

            SceneInfoDto sceneInfoDto = SceneEnum.getSceneInfoById(sceneId) ;
            SceneAgent sceneAgent = SceneEnum.getAgentById(agentTypeId) ;

            agentSceneEntity.setAgentId(roleId);

            agentSceneEntity.setSceneId(sceneId);
            agentSceneEntity.setSceneCode(sceneInfoDto.getCode());

            agentSceneEntity.setAgentTypeId(agentTypeId);
            agentSceneEntity.setAgentTypeCode(sceneAgent.getCode());

            agentSceneEntity.setSortOrder((int) count);
            agentSceneEntity.setRoleOrganizationId(AgentConstants.STORE_EMPTY_ORG_ID); // 发布到公共角色场景

            agentSceneEntity.setLlmModelId(llmModelId);
            agentSceneEntity.setImageModelId(imageModelId);
            agentSceneEntity.setSceneScope(sceneScope);

            agentSceneEntity.setOrgId(orgId);

            saveOrUpdate(agentSceneEntity);

        }
    }

    @Override
    public void offlineScene(Long roleId) {
        // 将角色从场景中删除
        if(roleId != 0){
            LambdaQueryWrapper<AgentSceneEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(AgentSceneEntity::getAgentId, roleId);

            remove(lambdaQueryWrapper);
        }
    }

    @Override
    public List<IndustryRoleEntity> getRoleBySceneIdAndAgentType(long sceneId, long agentTypeId, long orgId) {

        if(sceneId != 0 && agentTypeId != 0){
//            LambdaQueryWrapper<AgentSceneEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//            lambdaQueryWrapper.eq(AgentSceneEntity::getSceneId, sceneId);
//            lambdaQueryWrapper.eq(AgentSceneEntity::getAgentTypeId, agentTypeId);

            LambdaQueryWrapper<AgentSceneEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(AgentSceneEntity::getSceneId, sceneId);
            lambdaQueryWrapper.eq(AgentSceneEntity::getAgentTypeId, agentTypeId);
            lambdaQueryWrapper.and(wrapper ->
                    wrapper.eq(AgentSceneEntity::getSceneScope, "public")
                            .or(subWrapper ->
                                    subWrapper.eq(AgentSceneEntity::getSceneScope, "org")
                                            .eq(AgentSceneEntity::getOrgId, orgId)
                            )
            );

            List<AgentSceneEntity> agentSceneEntities = list(lambdaQueryWrapper);

            // 获取到AgentId
            List<Long> agentIds = agentSceneEntities.stream().map(AgentSceneEntity::getAgentId).toList();
            if(!agentIds.isEmpty()){
                return industryRoleService.listByIds(agentIds);
            }
        }

        return Collections.emptyList() ;
    }

    @Override
    public AgentSceneEntity getByRoleAndScene(long roleId, long sceneId) {
        if(roleId != 0){
            LambdaQueryWrapper<AgentSceneEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(AgentSceneEntity::getAgentId, roleId);
            lambdaQueryWrapper.eq(AgentSceneEntity::getSceneId, sceneId);

            return getOne(lambdaQueryWrapper);
        }
        return null;
    }
}
