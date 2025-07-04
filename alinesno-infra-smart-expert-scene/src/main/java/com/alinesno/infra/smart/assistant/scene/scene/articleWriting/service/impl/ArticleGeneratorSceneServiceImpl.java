package com.alinesno.infra.smart.assistant.scene.scene.articleWriting.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.mapper.ArticleGenerateSceneMapper;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.service.IArticleWriterSceneService;
import com.alinesno.infra.smart.assistant.scene.common.examPaper.service.IExamQuestionService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.scene.dto.RoleListRequestDto;
import com.alinesno.infra.smart.scene.dto.UpdateSceneAgentDto;
import com.alinesno.infra.smart.scene.entity.ArticleGenerateSceneEntity;
import com.alinesno.infra.smart.utils.RoleUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 试卷场景服务实现类
 */
@Slf4j
@Service
public class ArticleGeneratorSceneServiceImpl extends IBaseServiceImpl<ArticleGenerateSceneEntity, ArticleGenerateSceneMapper> implements IArticleWriterSceneService {

    @Autowired
    private IIndustryRoleService roleService ;

    @Autowired
    private IExamQuestionService examQuestionService;

    @Override
    public void updateSceneAgents(UpdateSceneAgentDto dto) {
        long sceneId = dto.getSceneId() ;

        LambdaQueryWrapper<ArticleGenerateSceneEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleGenerateSceneEntity::getSceneId, sceneId) ;
        long count = count(wrapper) ;

        ArticleGenerateSceneEntity entity = new ArticleGenerateSceneEntity() ;
        if(count > 0){
            entity = getOne(wrapper) ;
        }

        long articleWriterEngineer = RoleUtils.findSelectAgentIdByCode(dto , "articleWriter") ;
        long articleLayoutDesignerEngineer = RoleUtils.findSelectAgentIdByCode(dto , "articleLayoutDesigner") ;

        entity.setArticleWriterEngineer(articleWriterEngineer); ;
        entity.setArticleLayoutDesignerEngineer(articleLayoutDesignerEngineer); ;

        entity.setSceneId(sceneId);

        saveOrUpdate(entity) ;
    }

    @Override
    public List<IndustryRoleEntity> getRoleList(RoleListRequestDto dto) {
        PermissionQuery query = new PermissionQuery() ;
        BeanUtils.copyProperties(dto , query) ;

        ArticleGenerateSceneEntity entity = getBySceneId(dto.getSceneId(), query);
        if(entity == null){
            return Collections.emptyList() ;
        }

        String agentTypeCode = dto.getAgentTypeCode() ;

        if("articleWriter".equals(agentTypeCode)){
            return roleService.listByIds(List.of(entity.getArticleWriterEngineer()));
        }else if("articleLayoutDesigner".equals(agentTypeCode)){
            return roleService.listByIds(List.of(entity.getArticleLayoutDesignerEngineer()));
        }

        return Collections.emptyList() ;
    }


    @Override
    public ArticleGenerateSceneEntity getBySceneId(long sceneId, PermissionQuery query) {

        LambdaQueryWrapper<ArticleGenerateSceneEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleGenerateSceneEntity::getSceneId, sceneId) ;

        return getOne(wrapper) ;
    }

}
