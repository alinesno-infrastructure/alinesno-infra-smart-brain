package com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.scene.common.utils.RoleUtils;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.DocReviewInitDto;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.mapper.DocReviewSceneMapper;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewSceneService;
import com.alinesno.infra.smart.scene.dto.UpdateSceneAgentDto;
import com.alinesno.infra.smart.scene.entity.DocReviewSceneEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DocReviewSceneServiceImpl extends IBaseServiceImpl<DocReviewSceneEntity, DocReviewSceneMapper> implements IDocReviewSceneService {

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
    public DocReviewSceneEntity getBySceneId(long sceneId) {

        LambdaQueryWrapper<DocReviewSceneEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocReviewSceneEntity::getSceneId, sceneId) ;

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

}
