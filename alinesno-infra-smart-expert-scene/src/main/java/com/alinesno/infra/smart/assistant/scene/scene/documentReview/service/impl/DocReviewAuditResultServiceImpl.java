package com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.GenAuditResultDto;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.mapper.DocReviewAuditResultMapper;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewAuditResultService;
import com.alinesno.infra.smart.scene.entity.DocReviewAuditResultEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DocReviewAuditResultServiceImpl extends IBaseServiceImpl<DocReviewAuditResultEntity, DocReviewAuditResultMapper> implements IDocReviewAuditResultService {

    @Override
    public void saveAuditResult(List<DocReviewAuditResultEntity> auditResultList, GenAuditResultDto dto) {
        // 先删除sceneId下面的所有审核结果
        LambdaUpdateWrapper<DocReviewAuditResultEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(DocReviewAuditResultEntity::getSceneId, dto.getSceneId()) ;
        wrapper.eq(DocReviewAuditResultEntity::getRuleId, dto.getRuleId()) ;
        remove(wrapper) ;

        saveBatch(auditResultList) ;
    }

    @Override
    public List<DocReviewAuditResultEntity> getAuditResultByRuleId(long ruleId, long sceneId, long taskId) {

        LambdaQueryWrapper<DocReviewAuditResultEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocReviewAuditResultEntity::getRuleId, ruleId) ;
        wrapper.eq(DocReviewAuditResultEntity::getSceneId, sceneId) ;
        wrapper.eq(DocReviewAuditResultEntity::getTaskId, taskId) ;

        return list(wrapper) ;
    }

    @Override
    public List<DocReviewAuditResultEntity> getBySceneIdAndDocReviewSceneId(Long sceneId, Long docReviewSceneId, long taskId) {
        LambdaQueryWrapper<DocReviewAuditResultEntity> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(DocReviewAuditResultEntity::getSceneId, sceneId) ;
        wrapper.eq(DocReviewAuditResultEntity::getTaskId, taskId) ;
        wrapper.eq(DocReviewAuditResultEntity::getDocReviewSceneId, docReviewSceneId) ;

        return list(wrapper) ;
    }
}
