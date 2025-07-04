package com.alinesno.infra.smart.assistant.scene.scene.productResearch.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.scene.common.examPaper.service.IExamQuestionService;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.mapper.ProjectResearchSceneMapper;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.service.IProjectResearchSceneService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.scene.dto.RoleListRequestDto;
import com.alinesno.infra.smart.scene.dto.UpdateSceneAgentDto;
import com.alinesno.infra.smart.scene.entity.ProjectResearchSceneEntity;
import com.alinesno.infra.smart.utils.RoleUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class ProjectResearchSceneServiceImpl extends IBaseServiceImpl<ProjectResearchSceneEntity, ProjectResearchSceneMapper> implements IProjectResearchSceneService {

    @Autowired
    private IIndustryRoleService roleService ;

    @Autowired
    private IExamQuestionService examQuestionService;

    @Override
    public void updateSceneAgents(UpdateSceneAgentDto dto) {
        long sceneId = dto.getSceneId() ;

        LambdaQueryWrapper<ProjectResearchSceneEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectResearchSceneEntity::getSceneId, sceneId) ;
        long count = count(wrapper) ;

        ProjectResearchSceneEntity entity = new ProjectResearchSceneEntity() ;
        if(count > 0){
            entity = getOne(wrapper) ;
        }

        long progressCollectorEngineer = RoleUtils.findSelectAgentIdByCode(dto , "progressCollector") ;
        long progressAnalyzerEngineer = RoleUtils.findSelectAgentIdByCode(dto , "progressAnalyzer") ;

        entity.setProcessCollectorEngineer(progressCollectorEngineer); ;
        entity.setProgressAnalyzerEngineer(progressAnalyzerEngineer); ;

        entity.setSceneId(sceneId);

        saveOrUpdate(entity) ;
    }

    @Override
    public List<IndustryRoleEntity> getRoleList(RoleListRequestDto dto) {
        PermissionQuery query = new PermissionQuery() ;
        BeanUtils.copyProperties(dto , query) ;

        ProjectResearchSceneEntity entity = getBySceneId(dto.getSceneId(), query);
        if(entity == null){
            return Collections.emptyList() ;
        }

        String agentTypeCode = dto.getAgentTypeCode() ;

        if("progressCollector".equals(agentTypeCode)){
            return roleService.listByIds(List.of(entity.getProcessCollectorEngineer()));
        }else if("progressAnalyzer".equals(agentTypeCode)){
            return roleService.listByIds(List.of(entity.getProgressAnalyzerEngineer()));
        }

        return Collections.emptyList() ;
    }


    @Override
    public ProjectResearchSceneEntity getBySceneId(long sceneId, PermissionQuery query) {

        LambdaQueryWrapper<ProjectResearchSceneEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectResearchSceneEntity::getSceneId, sceneId) ;

        return getOne(wrapper) ;
    }

}