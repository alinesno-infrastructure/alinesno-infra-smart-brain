package com.alinesno.infra.smart.assistant.scene.scene.documentReader.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.scene.common.utils.RoleUtils;
import com.alinesno.infra.smart.assistant.scene.scene.documentReader.dto.DocReaderInitDto;
import com.alinesno.infra.smart.assistant.scene.scene.documentReader.mapper.DocReaderSceneMapper;
import com.alinesno.infra.smart.assistant.scene.scene.documentReader.service.IDocReaderSceneService;
import com.alinesno.infra.smart.scene.dto.UpdateSceneAgentDto;
import com.alinesno.infra.smart.scene.entity.DocReaderSceneEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DocReaderSceneServiceImpl extends IBaseServiceImpl<DocReaderSceneEntity, DocReaderSceneMapper> implements IDocReaderSceneService {

    @Override
    public DocReaderSceneEntity getBySceneId(long sceneId) {

        LambdaQueryWrapper<DocReaderSceneEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocReaderSceneEntity::getSceneId, sceneId) ;

        return getOne(wrapper) ;
    }

    @Override
    public void initAgents(DocReaderInitDto dto) {
        long sceneId = dto.getSceneId() ;

        LambdaQueryWrapper<DocReaderSceneEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocReaderSceneEntity::getSceneId, sceneId) ;
        long count = count(wrapper) ;

        DocReaderSceneEntity entity = new DocReaderSceneEntity() ;
        if(count > 0){
            entity = getOne(wrapper) ;
        }

        entity.setSummaryAgentEngineer(dto.getSummaryAgentEngineer());
        entity.setCaseQueryAgentEngineer(dto.getCaseQueryAgentEngineer());
        entity.setSceneId(sceneId);

        saveOrUpdate(entity) ;
    }

    @Override
    public void updateSceneAgents(UpdateSceneAgentDto dto) {
        long sceneId = dto.getSceneId() ;

        LambdaQueryWrapper<DocReaderSceneEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocReaderSceneEntity::getSceneId, sceneId) ;
        long count = count(wrapper) ;

        DocReaderSceneEntity entity = new DocReaderSceneEntity() ;
        if(count > 0){
            entity = getOne(wrapper) ;
        }

        long summaryAgentEngineer = RoleUtils.findSelectAgentIdByCode(dto , "summaryAgent") ;
        long caseQueryAgentEngineer = RoleUtils.findSelectAgentIdByCode(dto , "caseQueryAgent") ;

        entity.setSummaryAgentEngineer(summaryAgentEngineer) ;
        entity.setCaseQueryAgentEngineer(caseQueryAgentEngineer) ;
        entity.setSceneId(sceneId);

        saveOrUpdate(entity) ;
    }
}
