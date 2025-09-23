package com.alinesno.infra.smart.assistant.scene.scene.documentReader.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.scene.scene.documentReader.dto.DocReaderInitDto;
import com.alinesno.infra.smart.assistant.scene.scene.documentReader.mapper.DocReaderSceneMapper;
import com.alinesno.infra.smart.assistant.scene.scene.documentReader.service.IDocReaderSceneService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.scene.dto.RoleListRequestDto;
import com.alinesno.infra.smart.scene.dto.UpdateSceneAgentDto;
import com.alinesno.infra.smart.scene.entity.DocReaderSceneEntity;
import com.alinesno.infra.smart.utils.RoleUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class DocReaderSceneServiceImpl extends IBaseServiceImpl<DocReaderSceneEntity, DocReaderSceneMapper> implements IDocReaderSceneService {

    @Autowired
    private IIndustryRoleService roleService ;

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

    @Override
    public List<IndustryRoleEntity> getRoleList(RoleListRequestDto dto) {

        DocReaderSceneEntity entity = getBySceneId(dto.getSceneId()) ;

        String agentTypeCode = dto.getSceneTypeCode() ;

        if("summaryAgent".equals(agentTypeCode)){
            return roleService.listByIds(List.of(entity.getSummaryAgentEngineer())) ;
        }else if("caseQueryAgent".equals(agentTypeCode)){
            return roleService.listByIds(List.of(entity.getCaseQueryAgentEngineer())) ;
        }

        return Collections.emptyList() ;
    }
}
