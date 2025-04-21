package com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.scene.common.utils.GenPdfTool;
import com.alinesno.infra.smart.assistant.scene.common.utils.RoleUtils;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.controller.ReviewListOptionEnum;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.DocReviewInitDto;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.DocReviewRulesDto;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.DocReviewSceneInfoDto;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.mapper.DocReviewSceneMapper;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewAuditResultService;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewAuditService;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.dto.LongTextSceneDto;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.scene.dto.RoleListRequestDto;
import com.alinesno.infra.smart.scene.dto.SceneInfoDto;
import com.alinesno.infra.smart.scene.dto.UpdateSceneAgentDto;
import com.alinesno.infra.smart.scene.entity.DocReviewAuditResultEntity;
import com.alinesno.infra.smart.scene.entity.DocReviewRulesEntity;
import com.alinesno.infra.smart.scene.entity.DocReviewSceneEntity;
import com.alinesno.infra.smart.scene.entity.SceneEntity;
import com.alinesno.infra.smart.scene.enums.ContractTypeEnum;
import com.alinesno.infra.smart.scene.enums.SceneEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.tools.ant.util.DateUtils;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class DocReviewSceneServiceImpl extends IBaseServiceImpl<DocReviewSceneEntity, DocReviewSceneMapper> implements IDocReviewSceneService {

    @Autowired
    private IIndustryRoleService roleService ;

    @Autowired
    private IDocReviewAuditService docReviewAuditService ;

    @Autowired
    private IDocReviewAuditResultService docReviewAuditResultService ;

    @Override
    public void initAgents(DocReviewInitDto dto) {

        long sceneId = dto.getSceneId() ;

        LambdaQueryWrapper<DocReviewSceneEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocReviewSceneEntity::getSceneId, sceneId) ;
        long count = count(wrapper) ;

        DocReviewSceneEntity entity = new DocReviewSceneEntity() ;
        if(count > 0){
            entity = getOne(wrapper) ;
        }

        entity.setAnalysisAgentEngineer(dto.getAnalysisAgentEngineer());
        entity.setLogicReviewerEngineer(dto.getLogicReviewerEngineer());
        entity.setSceneId(sceneId);

        saveOrUpdate(entity) ;

    }

    @Override
    public DocReviewSceneEntity getBySceneId(long sceneId, PermissionQuery query) {

        LambdaQueryWrapper<DocReviewSceneEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocReviewSceneEntity::getSceneId, sceneId) ;

        // TODO fix:处理组织过滤的问题
//        wrapper.eq(DocReviewSceneEntity::getOrgId, query.getOrgId());

        return getOne(wrapper) ;
    }

    @Override
    public void updateSceneAgents(UpdateSceneAgentDto dto) {
        long sceneId = dto.getSceneId() ;

        LambdaQueryWrapper<DocReviewSceneEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocReviewSceneEntity::getSceneId, sceneId) ;
        long count = count(wrapper) ;

        DocReviewSceneEntity entity = new DocReviewSceneEntity() ;
        if(count > 0){
            entity = getOne(wrapper) ;
        }

        long analysisAgentEngineer = RoleUtils.findSelectAgentIdByCode(dto , "analysisAgent") ;
        long logicReviewerEngineer = RoleUtils.findSelectAgentIdByCode(dto , "logicReviewer") ;

        entity.setAnalysisAgentEngineer(analysisAgentEngineer) ;
        entity.setLogicReviewerEngineer(logicReviewerEngineer) ;
        entity.setSceneId(sceneId);

        saveOrUpdate(entity) ;
    }

    @Override
    public List<IndustryRoleEntity> getRoleList(RoleListRequestDto dto) {

        PermissionQuery query = new PermissionQuery() ;
        BeanUtils.copyProperties(dto , query) ;

        DocReviewSceneEntity entity = getBySceneId(dto.getSceneId(), query);
        if(entity == null){
            return Collections.emptyList() ;
        }

        String agentTypeCode = dto.getAgentTypeCode() ;

        if("analysisAgent".equals(agentTypeCode)){
            return roleService.listByIds(List.of(entity.getAnalysisAgentEngineer()));
        }else if("logicReviewer".equals(agentTypeCode)){
            return roleService.listByIds(List.of(entity.getLogicReviewerEngineer()));
        }

        return Collections.emptyList() ;
    }

    @Override
    @NotNull
    public DocReviewSceneInfoDto getDocReviewSceneInfoDto(long id, SceneEntity entity) {
        LongTextSceneDto dto = new LongTextSceneDto();
        BeanUtils.copyProperties(entity, dto);

        SceneInfoDto sceneInfoDto = SceneEnum.getSceneInfoByCode(entity.getSceneType());

        dto.setAgents(sceneInfoDto.getAgents());
        dto.setSceneId(sceneInfoDto.getId());

        DocReviewSceneInfoDto docSceneInfoDto = new DocReviewSceneInfoDto();
        BeanUtils.copyProperties(dto, docSceneInfoDto);

        // 查询出Entity信息
        PermissionQuery query = new PermissionQuery() ;
        BeanUtils.copyProperties(dto , query) ;
        DocReviewSceneEntity docReviewSceneEntity = getBySceneId(id , query);

        if(docReviewSceneEntity != null){

            docSceneInfoDto.setAnalysisAgentEngineer(docReviewSceneEntity.getAnalysisAgentEngineer());
            docSceneInfoDto.setAnalysisAgentEngineerEntity(roleService.getById(docReviewSceneEntity.getAnalysisAgentEngineer()));

            docSceneInfoDto.setLogicReviewerEngineer(docReviewSceneEntity.getLogicReviewerEngineer());
            docSceneInfoDto.setLogicReviewerEngineerEntity(roleService.getById(docReviewSceneEntity.getLogicReviewerEngineer()));

            docSceneInfoDto.setAuditId(docReviewSceneEntity.getAuditId());
            docSceneInfoDto.setContractType(docReviewSceneEntity.getContractType());
            docSceneInfoDto.setReviewPosition(docReviewSceneEntity.getReviewPosition()) ;
            docSceneInfoDto.setReviewListKnowledgeBase(docReviewSceneEntity.getReviewListKnowledgeBase());
            docSceneInfoDto.setContractOverview(docReviewSceneEntity.getContractOverview());
            docSceneInfoDto.setDocumentId(docReviewSceneEntity.getDocumentId());
            docSceneInfoDto.setReviewListOption(docReviewSceneEntity.getReviewListOption());

            docSceneInfoDto.setReviewList(docReviewSceneEntity.getReviewList());
            docSceneInfoDto.setDocumentName(docReviewSceneEntity.getDocumentName());
            docSceneInfoDto.setReviewListDtos(JSONArray.parseArray(docReviewSceneEntity.getReviewList(), DocReviewRulesDto.class));
            docSceneInfoDto.setGenStatus(docReviewSceneEntity.getGenStatus());
        }

        docSceneInfoDto.setContractTypes(ContractTypeEnum.getContractScenarioList());
        return docSceneInfoDto;
    }

    @Override
    public DocReviewSceneInfoDto getDocReviewSceneInfoDtoWithResultCount(long id, SceneEntity entity) {
        DocReviewSceneInfoDto docSceneInfoDto = getDocReviewSceneInfoDto(id , entity) ;

        List<DocReviewAuditResultEntity> auditResultList = docReviewAuditResultService.list(
                new LambdaQueryWrapper<DocReviewAuditResultEntity>()
                        .eq(DocReviewAuditResultEntity::getSceneId, id)
        );

        if(docSceneInfoDto.getReviewListDtos() != null){
            for (DocReviewRulesDto reviewListDto : docSceneInfoDto.getReviewListDtos()) {
                int count = getAuditResultCount(auditResultList , reviewListDto.getId()) ;
                reviewListDto.setAuditResultCount(count); ;
            }
        }

        return docSceneInfoDto ;
    }

    private int getAuditResultCount(List<DocReviewAuditResultEntity> auditResultList, Long id) {
        int count = 0 ;

        for (DocReviewAuditResultEntity auditResult : auditResultList) {
            if(auditResult.getRuleId().equals(id)){
                count ++ ;
            }
        }

        return count ;
    }

    @Override
    public String genMarkdownReport(long sceneId, PermissionQuery query, Long docReviewSceneId) {

        DocReviewSceneEntity docReviewSceneEntity = getById(docReviewSceneId) ;

        // 获取到所有的审核结果
        List<DocReviewAuditResultEntity> auditResultList = docReviewAuditResultService.list(
                new LambdaQueryWrapper<DocReviewAuditResultEntity>()
                        .eq(DocReviewAuditResultEntity::getOrgId, query.getOrgId())
                        .eq(DocReviewAuditResultEntity::getSceneId, sceneId)
                        .eq(DocReviewAuditResultEntity::getDocReviewSceneId, docReviewSceneId)
        );

        // 审核markdown封面信息
        String documentName = docReviewSceneEntity.getDocumentName() ;  // 审核文件名称
        String auditOption = docReviewSceneEntity.getReviewListOption() ;  // 审核方式
        String dateTime = DateUtils.format(docReviewSceneEntity.getAddTime(), DateUtils.ISO8601_DATETIME_PATTERN) ;  // 审核时间

        // 规则清单
        List<DocReviewRulesEntity> rules;
        if(docReviewSceneEntity.getReviewListOption().equals(ReviewListOptionEnum.AIGEN.getValue())){  // 使用AI生成的审核清单
            // 解析获取得到审核内容
            rules = JSONArray.parseArray(docReviewSceneEntity.getReviewList(), DocReviewRulesEntity.class) ;
        }else if(docReviewSceneEntity.getReviewListOption().equals(ReviewListOptionEnum.DATASET.getValue())) {  // 使用自定义的审核清单
            // 以逗号分隔，获取规则 ID 列表
            rules = docReviewAuditService.getRulesByAuditId(docReviewSceneEntity.getAuditId()) ;
        } else {
            rules = null;
        }

        // 整理在markdown格式
        StringBuilder markdown = GenPdfTool.getGenDocReviewMarkdownReport(documentName, auditOption, dateTime, rules, auditResultList);

        return markdown.toString();
    }


}
