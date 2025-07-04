package com.alinesno.infra.smart.assistant.scene.scene.generalAgent.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.scene.scene.generalAgent.mapper.GeneralAgentSceneMapper;
import com.alinesno.infra.smart.assistant.scene.scene.generalAgent.service.IGeneralAgentPlanService;
import com.alinesno.infra.smart.assistant.scene.scene.generalAgent.service.IGeneralAgentSceneService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.scene.dto.RoleListRequestDto;
import com.alinesno.infra.smart.scene.dto.UpdateSceneAgentDto;
import com.alinesno.infra.smart.scene.entity.GeneralAgentPlanEntity;
import com.alinesno.infra.smart.scene.entity.GeneralAgentSceneEntity;
import com.alinesno.infra.smart.utils.RoleUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class GeneralAgentSceneServiceImpl extends IBaseServiceImpl<GeneralAgentSceneEntity, GeneralAgentSceneMapper> implements IGeneralAgentSceneService {

    @Autowired
    private IIndustryRoleService roleService ;

    @Autowired
    private IGeneralAgentPlanService generalAgentPlanService ;

    @Override
    public GeneralAgentSceneEntity getBySceneId(long sceneId , PermissionQuery query) {

        LambdaQueryWrapper<GeneralAgentSceneEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GeneralAgentSceneEntity::getSceneId, sceneId) ;
//        wrapper.eq(GeneralAgentSceneEntity::getOrgId, query.getOrgId()) ;

        List<GeneralAgentSceneEntity> list =  list(wrapper) ;

        return list.isEmpty() ? null : list.get(0) ;
    }

//    @Override
//    public void initAgents(GeneralAgentInitDto dto) {
//        long sceneId = dto.getSceneId() ;
//
//        LambdaQueryWrapper<GeneralAgentSceneEntity> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(GeneralAgentSceneEntity::getSceneId, sceneId) ;
//        long count = count(wrapper) ;
//
//        GeneralAgentSceneEntity entity = new GeneralAgentSceneEntity() ;
//        if(count > 0){
//            entity = getOne(wrapper) ;
//        }
//
//        entity.setBusinessProcessorEngineer(String.valueOf(dto.getBusinessProcessorEngineer()));
//        entity.setBusinessExecuteEngineer(String.valueOf(dto.getBusinessExecuteEngineer()));
//        entity.setDataViewerEngineer(String.valueOf(dto.getDataViewerEngineer()));
//
//        entity.setSceneId(sceneId);
//        saveOrUpdate(entity) ;
//    }

    @Override
    public void updateSceneAgents(UpdateSceneAgentDto dto) {
        long sceneId = dto.getSceneId() ;

        LambdaQueryWrapper<GeneralAgentSceneEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GeneralAgentSceneEntity::getSceneId, sceneId) ;
        long count = count(wrapper) ;

        GeneralAgentSceneEntity entity = new GeneralAgentSceneEntity() ;
        if(count > 0){
            entity = getOne(wrapper) ;
        }

        Long businessProcessorEngineer = RoleUtils.findSelectAgentIdByCode(dto , "businessProcessor") ;
        Long dataViewerEngineer = RoleUtils.findSelectAgentIdByCode(dto , "dataViewer") ;
        Long businessExecuteEngineer = RoleUtils.findSelectAgentIdByCode(dto , "businessExecute") ;

        entity.setBusinessProcessorEngineer(String.valueOf(businessProcessorEngineer)); ;
        entity.setDataViewerEngineer(String.valueOf(dataViewerEngineer)); ;
        entity.setBusinessExecuteEngineer(String.valueOf(businessExecuteEngineer));
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
        GeneralAgentSceneEntity entity = getBySceneId(dto.getSceneId() , query) ;

        if(entity != null){
            String agentTypeCode = dto.getAgentTypeCode() ;

            if("businessProcessor".equals(agentTypeCode)){
                return roleService.listByIds(List.of(entity.getBusinessProcessorEngineer())) ;
            }else if("businessExecute".equals(agentTypeCode)){
                return roleService.listByIds(List.of(entity.getBusinessExecuteEngineer())) ;
            }else if("dataViewer".equals(agentTypeCode)){
                return roleService.listByIds(List.of(entity.getDataViewerEngineer())) ;
            }
        }

        return Collections.emptyList() ;
    }

    @Override
    public String genMarkdownContent(long sceneId, long taskId , PermissionQuery query, Long generalAgentSceneId) {

        LambdaQueryWrapper<GeneralAgentPlanEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GeneralAgentPlanEntity::getSceneId, sceneId);
        lambdaQueryWrapper.eq(GeneralAgentPlanEntity::getTaskId, taskId);

        // TODO fix:待处理orgId为0的问题
        // lambdaQueryWrapper.eq(GeneralAgentPlanEntity::getOrgId, query.getOrgId()) ;

        lambdaQueryWrapper.eq(GeneralAgentPlanEntity::getGeneralAgentSceneId, generalAgentSceneId) ;
        lambdaQueryWrapper.orderByAsc(GeneralAgentPlanEntity::getPlanSort); // 按照排序字段升序排列

        List<GeneralAgentPlanEntity> allChapters = generalAgentPlanService.list(lambdaQueryWrapper);

        // 构建树结构
        Map<Long, GeneralAgentPlanEntity> chapterMap = new HashMap<>();
        List<GeneralAgentPlanEntity> topChapters = new ArrayList<>();

        for (GeneralAgentPlanEntity chapter : allChapters) {
            chapterMap.put(chapter.getId(), chapter);
        }

        for (GeneralAgentPlanEntity chapter : allChapters) {
            Long parentId = chapter.getParentPlanId() ;
            if (parentId == null) {
                topChapters.add(chapter);
            } else {
                GeneralAgentPlanEntity parent = chapterMap.get(parentId);
                if (parent != null) {
                    if (parent.getSubtitles() == null) {
                        parent.setSubtitles(new ArrayList<>());
                    }
                    parent.getSubtitles().add(chapter);
                }
            }
        }

        StringBuilder markdownBuilder = new StringBuilder();

        for (GeneralAgentPlanEntity chapter : topChapters) {
            buildMarkdownForChapter(chapter, markdownBuilder, 1);
        }

        return markdownBuilder.toString();
    }

    private void buildMarkdownForChapter(GeneralAgentPlanEntity chapter, StringBuilder markdownBuilder, int level) {
        // 根据层级添加标题符号
        markdownBuilder.append("#".repeat(Math.max(0, level)));
        markdownBuilder.append(" ").append(chapter.getPlanName()).append("\n\n");
        if (chapter.getContent() != null) {
            markdownBuilder.append(chapter.getContent()).append("\n\n");
        }

        if (chapter.getSubtitles() != null) {
            for (GeneralAgentPlanEntity subChapter : chapter.getSubtitles()) {
                buildMarkdownForChapter(subChapter, markdownBuilder, level + 1);
            }
        }
    }

}
