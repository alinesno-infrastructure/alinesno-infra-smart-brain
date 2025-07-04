package com.alinesno.infra.smart.assistant.scene.scene.prototypeDesign.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.scene.common.examPaper.service.IExamQuestionService;
import com.alinesno.infra.smart.assistant.scene.scene.prototypeDesign.mapper.PrototypeSceneMapper;
import com.alinesno.infra.smart.assistant.scene.scene.prototypeDesign.service.IPrototypeSceneService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.scene.dto.RoleListRequestDto;
import com.alinesno.infra.smart.scene.dto.UpdateSceneAgentDto;
import com.alinesno.infra.smart.scene.entity.PrototypeSceneEntity;
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
public class PrototypeSceneServiceImpl extends IBaseServiceImpl<PrototypeSceneEntity, PrototypeSceneMapper> implements IPrototypeSceneService {

    @Autowired
    private IIndustryRoleService roleService ;

    @Autowired
    private IExamQuestionService examQuestionService;

    @Override
    public void updateSceneAgents(UpdateSceneAgentDto dto) {
        long sceneId = dto.getSceneId() ;

        LambdaQueryWrapper<PrototypeSceneEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PrototypeSceneEntity::getSceneId, sceneId) ;
        long count = count(wrapper) ;

        PrototypeSceneEntity entity = new PrototypeSceneEntity() ;
        if(count > 0){
            entity = getOne(wrapper) ;
        }

        long requirementAnalyzer = RoleUtils.findSelectAgentIdByCode(dto , "requirementAnalyzer") ;
        long prototypeDesigner = RoleUtils.findSelectAgentIdByCode(dto , "prototypeDesigner") ;

        entity.setRequirementAnalyzer(requirementAnalyzer) ;
        entity.setPrototypeDesigner(prototypeDesigner) ;

        entity.setSceneId(sceneId);

        saveOrUpdate(entity) ;
    }

    @Override
    public List<IndustryRoleEntity> getRoleList(RoleListRequestDto dto) {
        PermissionQuery query = new PermissionQuery() ;
        BeanUtils.copyProperties(dto , query) ;

        PrototypeSceneEntity entity = getBySceneId(dto.getSceneId(), query);
        if(entity == null){
            return Collections.emptyList() ;
        }

        String agentTypeCode = dto.getAgentTypeCode() ;

        if("requirementAnalyzer".equals(agentTypeCode)){
            return roleService.listByIds(List.of(entity.getRequirementAnalyzer()));
        }else if("prototypeDesigner".equals(agentTypeCode)){
            return roleService.listByIds(List.of(entity.getPrototypeDesigner()));
        }

        return Collections.emptyList() ;
    }


    @Override
    public PrototypeSceneEntity getBySceneId(long sceneId, PermissionQuery query) {

        LambdaQueryWrapper<PrototypeSceneEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PrototypeSceneEntity::getSceneId, sceneId) ;

        return getOne(wrapper) ;
    }

}
