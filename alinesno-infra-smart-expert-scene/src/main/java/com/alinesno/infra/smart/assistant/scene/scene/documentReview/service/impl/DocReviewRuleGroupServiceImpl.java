package com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.DocReviewRuleGroupDto;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.DocReviewRulesDto;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.mapper.DocReviewRuleGroupMapper;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewRuleGroupService;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewRulesService;
import com.alinesno.infra.smart.scene.entity.DocReviewRuleGroupEntity;
import com.alinesno.infra.smart.scene.entity.DocReviewRulesEntity;
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
public class DocReviewRuleGroupServiceImpl extends IBaseServiceImpl<DocReviewRuleGroupEntity, DocReviewRuleGroupMapper> implements IDocReviewRuleGroupService {

    @Autowired
    private IDocReviewRulesService rulesService ;

    @Override
    public List<DocReviewRuleGroupDto> listGroup(PermissionQuery query) {

        // 查询所有组织下的规则组
        LambdaQueryWrapper<DocReviewRuleGroupEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocReviewRuleGroupEntity::getOrgId, query.getOrgId());

        List<DocReviewRuleGroupEntity> ruleGroups = list(wrapper);
        if (ruleGroups != null && !ruleGroups.isEmpty()) {

            return ruleGroups.stream()
                    .map(rule -> {
                        DocReviewRuleGroupDto dto = new DocReviewRuleGroupDto();

                        dto.setId(rule.getId());
                        dto.setGroupName(rule.getGroupName());

                        LambdaQueryWrapper<DocReviewRulesEntity> rulesWrapper = new LambdaQueryWrapper<>();
                        rulesWrapper.eq(DocReviewRulesEntity::getGroupId, rule.getId());
                        List<DocReviewRulesEntity> rules = rulesService.list(rulesWrapper);

                        if (rules != null && !rules.isEmpty()) {
                            dto.setRules(rules.stream()
                                    .map(r -> {
                                        DocReviewRulesDto rulesDto = new DocReviewRulesDto();
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
        rulesService.remove(new LambdaQueryWrapper<DocReviewRulesEntity>().eq(DocReviewRulesEntity::getGroupId, id));
    }

    @Override
    public void importExcelData(Map<String, List<Map<String, Object>>> result, PermissionQuery query) {
        int count = 1 ;

        for (Map.Entry<String, List<Map<String, Object>>> entry : result.entrySet()) {
            System.out.println("工作表名称: " + entry.getKey());

            // 判断分组是否已经在数据库中，如果存在则跳过，如果不存在则创建
            if (exists(new LambdaQueryWrapper<DocReviewRuleGroupEntity>()
                    .eq(DocReviewRuleGroupEntity::getOrgId, query.getOrgId())
                    .eq(DocReviewRuleGroupEntity::getGroupName, entry.getKey()))) {
                continue;
            }

            DocReviewRuleGroupEntity group = new DocReviewRuleGroupEntity();
            BeanUtils.copyProperties(query , group);

            group.setGroupName(entry.getKey());
            group.setGroupSort(count++);

            save(group);

            Long groupId = group.getId() ;

            for (Map<String, Object> row : entry.getValue()) {

                String ruleName= (String) row.get("ruleName");
                String ruleContent = (String) row.get("ruleDesc");
                String riskLevel = (String) row.get("riskLevel");

                DocReviewRulesEntity rulesEntity = new DocReviewRulesEntity();
                BeanUtils.copyProperties(query , rulesEntity);
                rulesEntity.setGroupId(groupId);

                rulesEntity.setRuleName(ruleName);
                rulesEntity.setRuleContent(ruleContent);
                rulesEntity.setRiskLevel(riskLevel);

                rulesService.save(rulesEntity);
            }
        }
    }

}
