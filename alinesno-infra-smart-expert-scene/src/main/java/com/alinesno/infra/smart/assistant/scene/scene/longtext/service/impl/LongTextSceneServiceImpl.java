package com.alinesno.infra.smart.assistant.scene.scene.longtext.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.scene.common.utils.RoleUtils;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.mapper.LongTextSceneMapper;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.service.ILongTextSceneService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.scene.dto.RoleListRequestDto;
import com.alinesno.infra.smart.scene.dto.UpdateSceneAgentDto;
import com.alinesno.infra.smart.scene.entity.LongTextSceneEntity;
import com.alinesno.infra.smart.scene.entity.SceneEntity;
import com.alinesno.infra.smart.scene.enums.SceneScopeType;
import com.alinesno.infra.smart.scene.service.ISceneService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 长文本场景服务实现类
 */
@Slf4j
@Service
public class LongTextSceneServiceImpl extends IBaseServiceImpl<LongTextSceneEntity, LongTextSceneMapper> implements ILongTextSceneService {

    @Autowired
    private IIndustryRoleService roleService ;

    @Override
    public LongTextSceneEntity getBySceneId(long sceneId, PermissionQuery query) {

        ISceneService sceneService = SpringUtil.getBean(ISceneService.class);
        SceneEntity sceneEntity = sceneService.getById(sceneId) ;


        LambdaQueryWrapper<LongTextSceneEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LongTextSceneEntity::getSceneId, sceneId);
        queryWrapper.eq(LongTextSceneEntity::getOrgId , query.getOrgId()) ;

        LongTextSceneEntity entity = getOne(queryWrapper);

        // TODO 待优化逻辑
        // 如果是公共，通过源信息配置，做拦截数据修改更新处理，修改编辑角色信息。
        if(sceneEntity.getSceneScope().equals(SceneScopeType.PUBLIC_SCENE.getValue())){

            LambdaQueryWrapper<LongTextSceneEntity> queryWrapper2 = new LambdaQueryWrapper<>();
            queryWrapper2.eq(LongTextSceneEntity::getSceneId, sceneId);
            queryWrapper2.eq(LongTextSceneEntity::getOrgId , sceneEntity.getOrgId()) ;
            LongTextSceneEntity orginLongTextSceneEntity = getOne(queryWrapper2) ;

            if(entity == null) {
                entity = new LongTextSceneEntity() ;
                entity.setChapterEditor(orginLongTextSceneEntity.getChapterEditor()) ;
                entity.setContentEditor(orginLongTextSceneEntity.getContentEditor()) ;
                entity.setSceneId(sceneId);

                entity.setDepartmentId(query.getDepartmentId());
                entity.setOrgId(query.getOrgId());
                entity.setOperatorId(query.getOperatorId());

                saveOrUpdate(entity) ;
            }else {
                if(!entity.getChapterEditor().equals(orginLongTextSceneEntity.getChapterEditor())  || !entity.getContentEditor().equals(orginLongTextSceneEntity.getContentEditor())){
                    entity.setChapterEditor(orginLongTextSceneEntity.getChapterEditor()) ;
                    entity.setContentEditor(orginLongTextSceneEntity.getContentEditor()) ;
                    saveOrUpdate(entity) ;
                }
            }
        }

        return entity ;
    }

    @Override
    public void updateSceneAgents(UpdateSceneAgentDto dto) {

        long sceneId = dto.getSceneId() ;

        LambdaQueryWrapper<LongTextSceneEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LongTextSceneEntity::getSceneId, sceneId) ;
        wrapper.eq(LongTextSceneEntity::getOrgId , dto.getOrgId()) ;
        long count = count(wrapper) ;

        LongTextSceneEntity entity = new LongTextSceneEntity() ;
        if(count > 0){
            entity = getOne(wrapper) ;
        }

        long chapterEditorEngineer = RoleUtils.findSelectAgentIdByCode(dto , "chapterEditor") ;
        long contentEditorEngineer = RoleUtils.findSelectAgentIdByCode(dto , "contentEditor") ;

        entity.setChapterEditor(String.valueOf(chapterEditorEngineer)) ;
        entity.setContentEditor(String.valueOf(contentEditorEngineer)) ;
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

        LongTextSceneEntity entity = getBySceneId(id , query);
        if(entity == null){
            return Collections.emptyList() ;
        }

        String agentTypeCode = dto.getAgentTypeCode() ;

        if("chapterEditor".equals(agentTypeCode) && entity.getChapterEditor() != null){
            return roleService.listByIds(Arrays.asList(entity.getChapterEditor().split(",")));
        }else if("contentEditor".equals(agentTypeCode) && entity.getContentEditor() != null){
            return roleService.listByIds(Arrays.asList(entity.getContentEditor().split(",")));
        }

        return Collections.emptyList() ;
    }
}
