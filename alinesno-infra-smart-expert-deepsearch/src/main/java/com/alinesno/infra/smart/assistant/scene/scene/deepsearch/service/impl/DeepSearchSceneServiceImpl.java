package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.mapper.DeepSearchSceneMapper;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.service.IDeepSearchSceneService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.scene.dto.RoleListRequestDto;
import com.alinesno.infra.smart.scene.dto.UpdateSceneAgentDto;
import com.alinesno.infra.smart.scene.entity.DeepSearchSceneEntity;
import com.alinesno.infra.smart.utils.RoleUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 深度搜索场景服务实现类
 */
@Slf4j
@Service
public class DeepSearchSceneServiceImpl extends IBaseServiceImpl<DeepSearchSceneEntity, DeepSearchSceneMapper> implements IDeepSearchSceneService {

    @Autowired
    private IIndustryRoleService roleService ;

    @Override
    public void updateSceneAgents(UpdateSceneAgentDto dto) {
        long sceneId = dto.getSceneId() ;

        LambdaQueryWrapper<DeepSearchSceneEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeepSearchSceneEntity::getSceneId, sceneId) ;
        long count = count(wrapper) ;

        DeepSearchSceneEntity entity = new DeepSearchSceneEntity() ;
        if(count > 0){
            entity = getOne(wrapper) ;
        }

        Long searchPlannerEngineer = RoleUtils.findSelectAgentIdByCode(dto , "searchPlanner") ;
        Long searchExecutorEngineer = RoleUtils.findSelectAgentIdByCode(dto , "searchExecutor") ;

        entity.setSearchPlannerEngineer(searchPlannerEngineer); ;
        entity.setSearchExecutorEngineer(searchExecutorEngineer); ;
        entity.setSceneId(sceneId);

        entity.setOrgId(dto.getOrgId());
        entity.setDepartmentId(dto.getDepartmentId());
        entity.setOperatorId(dto.getOperatorId());

        saveOrUpdate(entity) ;
    }

    @Override
    public List<IndustryRoleEntity> getRoleList(RoleListRequestDto dto) {
        PermissionQuery query = new PermissionQuery() ;
        BeanUtils.copyProperties(dto , query) ;
        DeepSearchSceneEntity entity = getBySceneId(dto.getSceneId() , query) ;

        if(entity != null){
            String agentTypeCode = dto.getAgentTypeCode() ;

            if("searchPlanner".equals(agentTypeCode)){
                return roleService.listByIds(List.of(entity.getSearchPlannerEngineer())) ;
            }else if("searchExecutor".equals(agentTypeCode)){
                return roleService.listByIds(List.of(entity.getSearchExecutorEngineer())) ;
            }
        }

        return Collections.emptyList() ;
    }

    @Override
    public DeepSearchSceneEntity getBySceneId(long sceneId, PermissionQuery query) {

        LambdaQueryWrapper<DeepSearchSceneEntity> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(DeepSearchSceneEntity::getSceneId, sceneId) ;
        wrapper.eq(DeepSearchSceneEntity::getOrgId, query.getOrgId()) ;

        return getOne(wrapper) ;
    }
}
