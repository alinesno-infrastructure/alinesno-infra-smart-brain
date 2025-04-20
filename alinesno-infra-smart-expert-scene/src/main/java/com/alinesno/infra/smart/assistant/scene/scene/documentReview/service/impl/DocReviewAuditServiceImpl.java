package com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.DocReviewAuditDto;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.DocReviewRulesDto;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.mapper.DocReviewAuditMapper;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewAuditService;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewRulesService;
import com.alinesno.infra.smart.scene.entity.DocReviewAuditEntity;
import com.alinesno.infra.smart.scene.entity.DocReviewRulesEntity;
import com.alinesno.infra.smart.scene.enums.SceneScopeType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DocReviewAuditServiceImpl extends IBaseServiceImpl<DocReviewAuditEntity, DocReviewAuditMapper> implements IDocReviewAuditService {

    @Autowired
    private IDocReviewRulesService rulesService;

    @Override
    public DocReviewAuditDto queryAudit(Long id) {

        DocReviewAuditDto auditDto = new DocReviewAuditDto();

        DocReviewAuditEntity auditEntity = getBaseMapper().selectById(id);
        BeanUtils.copyProperties(auditEntity, auditDto);

        List<Long> ruleIds = getRuleIds(auditEntity) ;

        if (ruleIds != null && !ruleIds.isEmpty()) {
            List<DocReviewRulesEntity> rules = rulesService.listByIds(ruleIds);
            if (rules != null && !rules.isEmpty()) {
                auditDto.setRules(rules.stream()
                        .map(r -> {
                            DocReviewRulesDto rulesDto = new DocReviewRulesDto();
                            BeanUtils.copyProperties(r, rulesDto);
                            return rulesDto;
                        }).toList()
                );
            }
        }

        return auditDto ;
    }

    @Override
    public Long saveOrUpdateAudit(DocReviewAuditDto dto) {
        DocReviewAuditEntity entity = new DocReviewAuditEntity() ;
        BeanUtils.copyProperties(dto , entity) ;

        // 处理id的问题
        if (!dto.getAuditLists().isEmpty()) {
            String ruleIds = dto.getAuditLists().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            entity.setAuditList(ruleIds);
        }

        saveOrUpdate(entity);

        return entity.getId();
    }

    @Override
    public List<DocReviewAuditDto> getAuditList(PermissionQuery query) {

        LambdaQueryWrapper<DocReviewAuditEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper.eq(DocReviewAuditEntity::getOrgId, query.getOrgId())
                        .eq(DocReviewAuditEntity::getDataScope, SceneScopeType.ORG_SCENE.getValue()))
                .or()
                .eq(DocReviewAuditEntity::getDataScope, SceneScopeType.PUBLIC_SCENE.getValue());

        List<DocReviewAuditEntity> list =  list(queryWrapper) ;

        return list.stream()
                .map(e -> {
                    DocReviewAuditDto dto = new DocReviewAuditDto();
                    BeanUtils.copyProperties(e, dto);
                    return dto;
                }).collect(Collectors.toList());
    }

    @Override
    public List<DocReviewRulesEntity> getRulesByAuditId(long auditId) {

        DocReviewAuditEntity audit = getById(auditId) ;

        // 以逗号分隔，获取规则 ID 列表
        List<Long> ruleIds = audit.getAuditList() != null ?
                Arrays.stream(audit.getAuditList().split(","))
                        .map(Long::parseLong)
                        .toList() :
                List.of();

        return rulesService.listByIds(ruleIds);
    }

    public static List<Long> getRuleIds(DocReviewAuditEntity auditEntity) {
        if (auditEntity == null || auditEntity.getAuditList() == null) {
            return new ArrayList<>();
        }
        return Arrays.stream(auditEntity.getAuditList().split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }
}
