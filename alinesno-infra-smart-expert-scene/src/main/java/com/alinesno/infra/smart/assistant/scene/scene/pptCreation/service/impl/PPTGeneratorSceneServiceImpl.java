package com.alinesno.infra.smart.assistant.scene.scene.pptCreation.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.service.IExamQuestionService;
import com.alinesno.infra.smart.assistant.scene.scene.pptCreation.mapper.PPTGenerateSceneMapper;
import com.alinesno.infra.smart.assistant.scene.scene.pptCreation.service.IPPTGeneratorSceneService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.scene.dto.RoleListRequestDto;
import com.alinesno.infra.smart.scene.dto.UpdateSceneAgentDto;
import com.alinesno.infra.smart.scene.entity.PPTGenerateSceneEntity;
import com.alinesno.infra.smart.utils.RoleUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 试卷场景服务实现类
 */
@Slf4j
@Service
public class PPTGeneratorSceneServiceImpl extends IBaseServiceImpl<PPTGenerateSceneEntity, PPTGenerateSceneMapper> implements IPPTGeneratorSceneService {

    @Autowired
    private IIndustryRoleService roleService ;

    @Autowired
    private IExamQuestionService examQuestionService;

    @Override
    public void updateSceneAgents(UpdateSceneAgentDto dto) {
        long sceneId = dto.getSceneId() ;

        LambdaQueryWrapper<PPTGenerateSceneEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PPTGenerateSceneEntity::getSceneId, sceneId) ;
        long count = count(wrapper) ;

        PPTGenerateSceneEntity entity = new PPTGenerateSceneEntity() ;
        if(count > 0){
            entity = getOne(wrapper) ;
        }

        long pptPlannerEngineer = RoleUtils.findSelectAgentIdByCode(dto , "pptPlanner") ;
        long pptGeneratorEngineer = RoleUtils.findSelectAgentIdByCode(dto , "pptGenerator") ;

        entity.setPptPlannerEngineer(pptPlannerEngineer) ;
        entity.setPptGeneratorEngineer(pptGeneratorEngineer) ;

        entity.setSceneId(sceneId);

        saveOrUpdate(entity) ;
    }

    @Override
    public List<IndustryRoleEntity> getRoleList(RoleListRequestDto dto) {
        PermissionQuery query = new PermissionQuery() ;
        BeanUtils.copyProperties(dto , query) ;

        PPTGenerateSceneEntity entity = getBySceneId(dto.getSceneId(), query);
        if(entity == null){
            return Collections.emptyList() ;
        }

        String agentTypeCode = dto.getAgentTypeCode() ;

        if("pptPlanner".equals(agentTypeCode)){
            return roleService.listByIds(List.of(entity.getPptPlannerEngineer()));
        }else if("pptGenerator".equals(agentTypeCode)){
            return roleService.listByIds(List.of(entity.getPptGeneratorEngineer()));
        }

        return Collections.emptyList() ;
    }


    @Override
    public PPTGenerateSceneEntity getBySceneId(long sceneId, PermissionQuery query) {

        LambdaQueryWrapper<PPTGenerateSceneEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PPTGenerateSceneEntity::getSceneId, sceneId) ;

        return getOne(wrapper) ;
    }

}
