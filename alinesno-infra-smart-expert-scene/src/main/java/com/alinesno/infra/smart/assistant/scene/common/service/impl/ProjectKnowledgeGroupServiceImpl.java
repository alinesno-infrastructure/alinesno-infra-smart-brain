package com.alinesno.infra.smart.assistant.scene.common.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.smart.assistant.scene.common.mapper.ProjectKnowledgeGroupMapper;
import com.alinesno.infra.smart.scene.dto.ProjectKnowledgeGroupDto;
import com.alinesno.infra.smart.scene.entity.ProjectKnowledgeEntity;
import com.alinesno.infra.smart.scene.entity.ProjectKnowledgeGroupEntity;
import com.alinesno.infra.smart.scene.service.IProjectKnowledgeGroupService;
import com.alinesno.infra.smart.scene.service.IProjectKnowledgeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ProjectKnowledgeGroupServiceImpl extends IBaseServiceImpl<ProjectKnowledgeGroupEntity, ProjectKnowledgeGroupMapper> implements IProjectKnowledgeGroupService {

    @Autowired
    private IProjectKnowledgeService knowledgeService ;

    @Override
    public List<ProjectKnowledgeGroupDto> listGroup(PermissionQuery query) {

        // 查询所有组织下的规则组
        LambdaQueryWrapper<ProjectKnowledgeGroupEntity> wrapper = new LambdaQueryWrapper<>();
        query.toWrapper(wrapper);
        wrapper.setEntityClass(ProjectKnowledgeGroupEntity.class);
        wrapper.orderByDesc(ProjectKnowledgeGroupEntity::getAddTime) ;

        List<ProjectKnowledgeGroupEntity> ruleGroups = list(wrapper);
        if (ruleGroups != null && !ruleGroups.isEmpty()) {

            return ruleGroups.stream()
                    .map(rule -> {
                        ProjectKnowledgeGroupDto dto = new ProjectKnowledgeGroupDto();

                        dto.setId(rule.getId());
                        dto.setGroupName(rule.getGroupName());
                        dto.setDescription(rule.getGroupDescription());
                        dto.setGroupType(rule.getGroupType());
                        dto.setAddTime(rule.getAddTime());

                        dto.setEmbeddingModelId(rule.getEmbeddingModelId());
                        dto.setDocumentRecognitionModelId(rule.getDocumentRecognitionModelId());
                        dto.setOcrModelId(rule.getOcrModelId());

                        LambdaQueryWrapper<ProjectKnowledgeEntity> knowledgeWrapper = new LambdaQueryWrapper<>();
                        knowledgeWrapper.eq(ProjectKnowledgeEntity::getGroupId, rule.getId());
                        long count = knowledgeService.count(knowledgeWrapper);
                        dto.setDocumentCount(count);

                        return dto;
                    })
                    .toList();
        }

        return Collections.emptyList() ;
    }

    @Override
    public void removeGroup(Long id) {
        // .先删除分组再删除分组下面的规则
        removeById(id);
        knowledgeService.remove(new LambdaQueryWrapper<ProjectKnowledgeEntity>().eq(ProjectKnowledgeEntity::getGroupId, id));
    }

    /**
     * 根据索引名称查询
     * @param collectionIndexName
     * @return
     */
    @Override
    public ProjectKnowledgeGroupEntity getByCollectionIndexName(String collectionIndexName) {
        return getOne(new LambdaQueryWrapper<ProjectKnowledgeGroupEntity>()
                .eq(ProjectKnowledgeGroupEntity::getVectorDatasetName, collectionIndexName));
    }

}