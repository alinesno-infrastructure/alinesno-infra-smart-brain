package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.enums.DataScopeType;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.enums.GroupTypeEnums;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.mapper.ContentFormatterLayoutGroupMapper;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.mapper.ContentFormatterRuleMapper;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.IContentFormatterRuleService;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.tools.ContentRuleExcelData;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.tools.ContentRuleExcelParser;
import com.alinesno.infra.smart.scene.entity.ContentFormatterLayoutGroupEntity;
import com.alinesno.infra.smart.scene.entity.ContentFormatterRuleEntity;
import com.alinesno.infra.smart.scene.enums.SceneScopeType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ContentFormatterRuleServiceImpl extends IBaseServiceImpl<ContentFormatterRuleEntity, ContentFormatterRuleMapper> implements IContentFormatterRuleService {

    @Autowired
    private ContentFormatterLayoutGroupMapper groupMapper;

    @Override
    public Map<String, Object> readExcel(List<ContentRuleExcelParser.TemplateBean> templates,
                                         PermissionQuery query) {

        List<ContentRuleExcelData> failedList = new ArrayList<>();
        List<ContentFormatterRuleEntity> successList = new ArrayList<>();
        Map<Long, List<ContentFormatterRuleEntity>> groupTemplateMap = new HashMap<>();

        try {

            // 1. 先检查导入数据中的重复规则名称
            checkDuplicateRuleNamesInImport(templates, failedList);
            if (!failedList.isEmpty()) {
                return buildResult(successList, failedList, groupTemplateMap);
            }

            List<ContentRuleExcelParser.TemplateGroup> fullGroups = ContentRuleExcelParser.getTemplateGroups(templates);

            for (ContentRuleExcelParser.TemplateGroup group : fullGroups) {
                try {

                    // Check if group already exists
                    LambdaQueryWrapper<ContentFormatterLayoutGroupEntity>  queryGroupWrapper = new LambdaQueryWrapper<>();
                    queryGroupWrapper.eq(ContentFormatterLayoutGroupEntity::getName, group.getGroupName());
                    queryGroupWrapper.eq(ContentFormatterLayoutGroupEntity::getGroupType, GroupTypeEnums.AUDIT.getValue());
                    queryGroupWrapper.eq(ContentFormatterLayoutGroupEntity::getDataScope, SceneScopeType.ORG_SCENE.getValue());
                    queryGroupWrapper.eq(ContentFormatterLayoutGroupEntity::getOrgId, query.getOrgId());

                    ContentFormatterLayoutGroupEntity existingGroup = groupMapper.selectOne(queryGroupWrapper);

                    ContentFormatterLayoutGroupEntity templateGroup;
                    if (existingGroup != null) {
                        templateGroup = existingGroup;
                        log.info("Using existing group: {}", group.getGroupName());
                    } else {
                        templateGroup = processTemplateGroup(group, query);
                    }

                    List<ContentFormatterRuleEntity> ruleEntities = processRulesWithDuplicateCheck(group, templateGroup, failedList, query);

                    groupTemplateMap.put(templateGroup.getId(), ruleEntities);
                    successList.addAll(ruleEntities);
                } catch (Exception e) {
                    addGroupToFailedList(group, failedList, "分组处理失败: " + e.getMessage());
                }
            }

            if (!successList.isEmpty()) {
                saveBatch(successList);
            }
        } catch (Exception e) {
            ContentRuleExcelData systemError = new ContentRuleExcelData();
            systemError.setFailReason(Map.of(
                    "error", "系统级错误: " + e.getClass().getSimpleName(),
                    "message", e.getMessage() != null ? e.getMessage() : "无详细错误信息",
                    "stacktrace", getSimplifiedStackTrace(e)
            ));
            failedList.add(systemError);
            log.error("Excel导入系统错误", e);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successList.size());
        result.put("failedCount", failedList.size());
        result.put("failedList", failedList);
        result.put("groupTemplateMap", groupTemplateMap);

        return result;
    }

    // 检查导入数据中的重复规则名称
    private void checkDuplicateRuleNamesInImport(List<ContentRuleExcelParser.TemplateBean> templates,
                                                 List<ContentRuleExcelData> failedList) {
        Map<String, Long> nameCount = templates.stream()
                .collect(Collectors.groupingBy(ContentRuleExcelParser.TemplateBean::getRuleName, Collectors.counting()));

        nameCount.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .forEach(entry -> {
                    String errorMsg = "导入数据中存在重复规则名称: " + entry.getKey();
                    templates.stream()
                            .filter(t -> t.getRuleName().equals(entry.getKey()))
                            .forEach(t -> failedList.add(createFailedRecord(t, errorMsg)));
                });
    }

    // 检查已存在的分组
    private ContentFormatterLayoutGroupEntity checkExistingGroup(ContentRuleExcelParser.TemplateGroup group,
                                                                 PermissionQuery query) {

      LambdaQueryWrapper<ContentFormatterLayoutGroupEntity> queryWrapper = new LambdaQueryWrapper<>();
      queryWrapper.eq(ContentFormatterLayoutGroupEntity::getName, group.getGroupName())
                .eq(ContentFormatterLayoutGroupEntity::getDataScope, SceneScopeType.ORG_SCENE.getValue())
                .eq(ContentFormatterLayoutGroupEntity::getOrgId, query.getOrgId());

      return groupMapper.selectOne(queryWrapper);

    }

    // 带重复检查的规则处理方法
    private List<ContentFormatterRuleEntity> processRulesWithDuplicateCheck(
            ContentRuleExcelParser.TemplateGroup group,
            ContentFormatterLayoutGroupEntity templateGroup,
            List<ContentRuleExcelData> failedList,
            PermissionQuery query) {

        return group.getTemplates().stream()
                .map(template -> {
                    try {
                        LambdaQueryWrapper<ContentFormatterRuleEntity> queryWrapper = new LambdaQueryWrapper<>();
                        queryWrapper.eq(ContentFormatterRuleEntity::getRuleName, template.getRuleName())
                                .eq(ContentFormatterRuleEntity::getGroupId, templateGroup.getId());
                        ContentFormatterRuleEntity existingRule = mapper.selectOne(queryWrapper);
                        if (existingRule != null) {
                            failedList.add(createFailedRecord(template, "规则已存在: " + template.getRuleName()));
                            return null;
                        }

                        return createRuleEntity(template, templateGroup, query);
                    } catch (Exception e) {
                        failedList.add(createFailedRecord(template,
                                "规则处理失败: " + e.getMessage()));
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void checkForDuplicatesInImportData(List<ContentRuleExcelParser.TemplateBean> templates,
                                                List<ContentRuleExcelData> failedList) {
        Map<String, Long> ruleNameCount = templates.stream()
                .collect(Collectors.groupingBy(ContentRuleExcelParser.TemplateBean::getRuleName, Collectors.counting()));

        ruleNameCount.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .forEach(entry -> {
                    String errorMsg = "导入数据中存在重复的规则名称: " + entry.getKey();
                    templates.stream()
                            .filter(t -> t.getRuleName().equals(entry.getKey()))
                            .forEach(t -> failedList.add(createFailedRecord(t, errorMsg)));
                });
    }

    private Map<String, Object> buildResult(List<ContentFormatterRuleEntity> successList,
                                            List<ContentRuleExcelData> failedList,
                                            Map<Long, List<ContentFormatterRuleEntity>> groupTemplateMap) {
        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successList.size());
        result.put("failedCount", failedList.size());
        result.put("failedList", failedList);
        result.put("groupTemplateMap", groupTemplateMap);
        return result;
    }

    private void addGroupToFailedList(ContentRuleExcelParser.TemplateGroup group,
                                      List<ContentRuleExcelData> failedList,
                                      String errorMessage) {
        for (ContentRuleExcelParser.TemplateBean template : group.getTemplates()) {
            failedList.add(createFailedRecord(template, errorMessage));
        }
    }

    private ContentFormatterLayoutGroupEntity processTemplateGroup(ContentRuleExcelParser.TemplateGroup group, PermissionQuery query) {
        ContentFormatterLayoutGroupEntity templateGroup = ContentFormatterLayoutGroupEntity.toPower(query);

        BeanUtil.copyProperties(query, templateGroup);
        templateGroup.setName(group.getGroupName());
        templateGroup.setGroupType(GroupTypeEnums.AUDIT.getValue());   // 审核分类
        templateGroup.setSort(0);
        templateGroup.setDataScope(SceneScopeType.ORG_SCENE.getValue());
        templateGroup.setIcon("AddLocation");

        groupMapper.insert(templateGroup);
        return templateGroup;
    }

    private List<ContentFormatterRuleEntity> processRules(ContentRuleExcelParser.TemplateGroup group,
                                                          ContentFormatterLayoutGroupEntity templateGroup,
                                                          List<ContentRuleExcelData> failedList,
                                                          PermissionQuery query) {
        List<ContentFormatterRuleEntity> ruleEntities = new ArrayList<>();

        for (ContentRuleExcelParser.TemplateBean template : group.getTemplates()) {
            try {
                ContentFormatterRuleEntity ruleEntity = createRuleEntity(template, templateGroup, query);
                ruleEntities.add(ruleEntity);
            } catch (Exception e) {
                failedList.add(createFailedRecord(template, "规则处理失败: " + e.getMessage()));
            }
        }

        return ruleEntities;
    }

    private ContentFormatterRuleEntity createRuleEntity(ContentRuleExcelParser.TemplateBean template,
                                                        ContentFormatterLayoutGroupEntity templateGroup,
                                                        PermissionQuery query) {
        ContentFormatterRuleEntity ruleEntity = new ContentFormatterRuleEntity();
        BeanUtil.copyProperties(query, ruleEntity);

        ruleEntity.setGroupId(templateGroup.getId());

        ruleEntity.setRuleName(template.getRuleName());
        ruleEntity.setRiskLevel(template.getRiskLevel());
        ruleEntity.setRuleContent(template.getRuleContent());
        ruleEntity.setReviewPosition(template.getReviewPosition());
        ruleEntity.setScope(template.getScope());
        ruleEntity.setRuleDescription(template.getRuleDescription());

        return ruleEntity;
    }

    private ContentRuleExcelData createFailedRecord(ContentRuleExcelParser.TemplateBean template, String errorMessage) {
        ContentRuleExcelData failedRecord = new ContentRuleExcelData();
        failedRecord.setRuleName(template.getRuleName());
        failedRecord.setFailReason(Map.of(
                "error", errorMessage,
                "ruleName", template.getRuleName(),
                "groupName", template.getGroupName()
        ));
        return failedRecord;
    }

    private String getSimplifiedStackTrace(Exception e) {
        return Arrays.stream(e.getStackTrace())
                .limit(5)
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));
    }
}