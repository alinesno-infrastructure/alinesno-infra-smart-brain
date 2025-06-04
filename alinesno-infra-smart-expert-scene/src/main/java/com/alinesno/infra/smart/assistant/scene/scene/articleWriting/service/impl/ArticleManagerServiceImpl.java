package com.alinesno.infra.smart.assistant.scene.scene.articleWriting.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.dto.ArticleGenerateSceneDto;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.dto.ArticleGeneratorDTO;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.dto.ArticleUpdateDto;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.mapper.ArticleManagerMapper;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.service.IArticleManagerService;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ExamPaperDTO;
import com.alinesno.infra.smart.scene.entity.ArticleGenerateSceneEntity;
import com.alinesno.infra.smart.scene.entity.ArticleManagerEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 试卷场景服务实现类
 */
@Slf4j
@Service
public class ArticleManagerServiceImpl extends IBaseServiceImpl<ArticleManagerEntity, ArticleManagerMapper> implements IArticleManagerService {

    @Override
    public ArticleGenerateSceneDto getPagerDetail(Long id) {
        return null;
    }

    @Override
    public void savePager(ExamPaperDTO dto) {

    }

    @Override
    public void updatePager(ArticleGenerateSceneDto dto) {

    }

    @Override
    public List<ArticleManagerEntity> pagerListByPage(DatatablesPageBean page, PermissionQuery query) {
        
        page.setPageNum(0);
        page.setPageSize(20);

        Page<ArticleManagerEntity> pageBean = new Page<>(page.getPageNum(), page.getPageSize());

        LambdaQueryWrapper<ArticleManagerEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleManagerEntity::getOrgId, query.getOrgId()) ;
        wrapper.orderByDesc(ArticleManagerEntity::getAddTime) ;

        pageBean = page(pageBean, wrapper);

        return pageBean.getRecords();
    }

    @Override
    public Long saveArticle(String articleContent, ArticleGeneratorDTO dto, ArticleGenerateSceneEntity entity, PermissionQuery query) {

        ArticleManagerEntity article = new ArticleManagerEntity() ;

        article.setSceneId(dto.getSceneId());
        article.setArticleGeneratorSceneId(entity.getId());

        article.setTitle(dto.getPromptText());
        article.setContent(articleContent);
        article.setTemplateId(dto.getSelectedTemplateId()) ;

        article.setOrgId(query.getOrgId());
        article.setOperatorId(query.getOperatorId());
        article.setDepartmentId(query.getDepartmentId());

        save(article);

        return article.getId() ;
    }

    @Override
    public void updateArticle(ArticleUpdateDto dto) {
        Long id = dto.getId();
        ArticleManagerEntity article = getById(id);
        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());
        article.setDescription(dto.getDescription());
        updateById(article);
    }

}
