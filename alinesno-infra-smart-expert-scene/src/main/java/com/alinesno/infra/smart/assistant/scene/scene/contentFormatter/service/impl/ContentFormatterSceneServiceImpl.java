package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.scene.common.utils.RoleUtils;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.mapper.ContentFormatterSceneMapper;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.IContentFormatterSceneService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.scene.dto.RoleListRequestDto;
import com.alinesno.infra.smart.scene.dto.UpdateSceneAgentDto;
import com.alinesno.infra.smart.scene.entity.ContentFormatterSceneEntity;
import com.alinesno.infra.smart.scene.entity.SceneEntity;
import com.alinesno.infra.smart.scene.enums.SceneScopeType;
import com.alinesno.infra.smart.scene.service.ISceneService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class ContentFormatterSceneServiceImpl extends IBaseServiceImpl<ContentFormatterSceneEntity , ContentFormatterSceneMapper> implements IContentFormatterSceneService {
    
    @Autowired
    private IIndustryRoleService roleService ;

    @Override
    public void updateSceneAgents(UpdateSceneAgentDto dto) {

        long sceneId = dto.getSceneId() ;

        LambdaQueryWrapper<ContentFormatterSceneEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ContentFormatterSceneEntity::getSceneId, sceneId) ;
        wrapper.eq(ContentFormatterSceneEntity::getOrgId , dto.getOrgId()) ;
        long count = count(wrapper) ;

        ContentFormatterSceneEntity entity = new ContentFormatterSceneEntity() ;
        if(count > 0){
            entity = getOne(wrapper) ;
        }

        long templateExtractorEngineer = RoleUtils.findSelectAgentIdByCode(dto , "templateExtractor") ;
        long contentReviewerEngineer = RoleUtils.findSelectAgentIdByCode(dto , "contentReviewer") ;

        entity.setTemplateExtractorEngineer(String.valueOf(templateExtractorEngineer)); ;
        entity.setContentReviewerEngineer(String.valueOf(contentReviewerEngineer)) ;
        entity.setSceneId(sceneId);

        entity.setDepartmentId(dto.getDepartmentId());
        entity.setOrgId(dto.getOrgId());
        entity.setOperatorId(dto.getOperatorId());

        saveOrUpdate(entity) ;

    }

    @Override
    public List<IndustryRoleEntity> getRoleList(RoleListRequestDto dto) {

        long id = dto.getSceneId();

        PermissionQuery query = new PermissionQuery() ;
        BeanUtils.copyProperties(dto , query) ;

        ContentFormatterSceneEntity entity = getBySceneId(id , query);
        if(entity == null){
            return Collections.emptyList() ;
        }

        String agentTypeCode = dto.getAgentTypeCode() ;

        if("templateExtractor".equals(agentTypeCode) && entity.getTemplateExtractorEngineer() != null) {
            return roleService.listByIds(Arrays.asList(entity.getTemplateExtractorEngineer().split(",")));
        }else if("contentReviewer".equals(agentTypeCode) && entity.getContentReviewerEngineer() != null){
            return roleService.listByIds(Arrays.asList(entity.getContentReviewerEngineer().split(",")));
        }

        return Collections.emptyList() ;
    }

    @Override
    public ContentFormatterSceneEntity getBySceneId(long sceneId, PermissionQuery query) {
        ISceneService sceneService = SpringUtil.getBean(ISceneService.class);
        SceneEntity sceneEntity = sceneService.getById(sceneId) ;


        LambdaQueryWrapper<ContentFormatterSceneEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ContentFormatterSceneEntity::getSceneId, sceneId);
        queryWrapper.eq(ContentFormatterSceneEntity::getOrgId , query.getOrgId()) ;

        ContentFormatterSceneEntity entity = getOne(queryWrapper);

        // TODO 待优化逻辑
        // 如果是公共，通过源信息配置，做拦截数据修改更新处理，修改编辑角色信息。
        if(sceneEntity.getSceneScope().equals(SceneScopeType.PUBLIC_SCENE.getValue())){

            LambdaQueryWrapper<ContentFormatterSceneEntity> queryWrapper2 = new LambdaQueryWrapper<>();
            queryWrapper2.eq(ContentFormatterSceneEntity::getSceneId, sceneId);
            queryWrapper2.eq(ContentFormatterSceneEntity::getOrgId , sceneEntity.getOrgId()) ;
            ContentFormatterSceneEntity orginContentFormatterSceneEntity = getOne(queryWrapper2) ;

            if(entity == null) {
                entity = new ContentFormatterSceneEntity() ;
                entity.setTemplateExtractorEngineer(orginContentFormatterSceneEntity.getTemplateExtractorEngineer()); ;
                entity.setSceneId(sceneId);

                entity.setDepartmentId(query.getDepartmentId());
                entity.setOrgId(query.getOrgId());
                entity.setOperatorId(query.getOperatorId());

                saveOrUpdate(entity) ;
            }else {
                if(!entity.getTemplateExtractorEngineer().equals(orginContentFormatterSceneEntity.getTemplateExtractorEngineer()) ||
                        !entity.getContentReviewerEngineer().equals(orginContentFormatterSceneEntity.getContentReviewerEngineer())){

                    entity.setTemplateExtractorEngineer(orginContentFormatterSceneEntity.getTemplateExtractorEngineer()); ;
                    entity.setContentReviewerEngineer(orginContentFormatterSceneEntity.getContentReviewerEngineer());
                    saveOrUpdate(entity) ;
                }
            }
        }

        return entity ;
    }
}