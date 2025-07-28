package com.alinesno.infra.smart.assistant.scene.common.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.ProjectKnowledgeDto;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.ProjectKnowledgeGroupDto;
import com.alinesno.infra.smart.assistant.scene.common.mapper.ProjectKnowledgeGroupMapper;
import com.alinesno.infra.smart.assistant.scene.common.service.IProjectKnowledgeGroupService;
import com.alinesno.infra.smart.assistant.scene.common.service.IProjectKnowledgeService;
import com.alinesno.infra.smart.scene.entity.ProjectKnowledgeEntity;
import com.alinesno.infra.smart.scene.entity.ProjectKnowledgeGroupEntity;
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
        wrapper.eq(ProjectKnowledgeGroupEntity::getOrgId, query.getOrgId());

        List<ProjectKnowledgeGroupEntity> ruleGroups = list(wrapper);
        if (ruleGroups != null && !ruleGroups.isEmpty()) {

            return ruleGroups.stream()
                    .map(rule -> {
                        ProjectKnowledgeGroupDto dto = new ProjectKnowledgeGroupDto();

                        dto.setId(rule.getId());
                        dto.setGroupName(rule.getGroupName());

                        LambdaQueryWrapper<ProjectKnowledgeEntity> rulesWrapper = new LambdaQueryWrapper<>();
                        rulesWrapper.eq(ProjectKnowledgeEntity::getGroupId, rule.getId());
                        List<ProjectKnowledgeEntity> rules = knowledgeService.list(rulesWrapper);

                        if (rules != null && !rules.isEmpty()) {
                            dto.setRules(rules.stream()
                                    .map(r -> {
                                        ProjectKnowledgeDto rulesDto = new ProjectKnowledgeDto();
                                        BeanUtils.copyProperties(r, rulesDto);
                                        return rulesDto;
                                    }).toList()
                            );
                        }else{
                            dto.setRules(Collections.emptyList());
                        }

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

    @Override
    public void importExcelData(Map<String, List<Map<String, Object>>> result, PermissionQuery query) {
        int count = 1 ;

        for (Map.Entry<String, List<Map<String, Object>>> entry : result.entrySet()) {

            // 判断分组是否已经在数据库中，如果存在则跳过，如果不存在则创建
            if (exists(new LambdaQueryWrapper<ProjectKnowledgeGroupEntity>()
                    .eq(ProjectKnowledgeGroupEntity::getOrgId, query.getOrgId())
                    .eq(ProjectKnowledgeGroupEntity::getGroupName, entry.getKey()))) {
                continue;
            }

            ProjectKnowledgeGroupEntity group = new ProjectKnowledgeGroupEntity();
            BeanUtils.copyProperties(query , group);

            group.setGroupName(entry.getKey());
            group.setGroupSort(count++);

            save(group);

            Long groupId = group.getId() ;

//            for (Map<String, Object> row : entry.getValue()) {
//
//                String ruleName= (String) row.get("ruleName");
//                String ruleContent = (String) row.get("ruleDesc");
//                String riskLevel = (String) row.get("riskLevel");
//
//                ProjectKnowledgeEntity rulesEntity = new ProjectKnowledgeEntity();
//                BeanUtils.copyProperties(query , rulesEntity);
//                rulesEntity.setGroupId(groupId);
//
//                rulesEntity.setRuleName(ruleName);
//                rulesEntity.setRuleContent(ruleContent);
//                rulesEntity.setRiskLevel(riskLevel);
//
//                rulesService.save(rulesEntity);
//            }

        }
    }

}