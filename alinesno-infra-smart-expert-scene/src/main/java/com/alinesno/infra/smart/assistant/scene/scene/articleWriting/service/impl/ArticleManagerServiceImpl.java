package com.alinesno.infra.smart.assistant.scene.scene.articleWriting.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.dto.ArticleGenerateSceneDto;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.dto.ArticleGeneratorDTO;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.dto.ArticleManagerResponseDto;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.dto.ArticleUpdateDto;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.mapper.ArticleManagerMapper;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.mapper.ArticleTemplateMapper;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.service.IArticleManagerService;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ExamPaperDTO;
import com.alinesno.infra.smart.scene.entity.ArticleGenerateSceneEntity;
import com.alinesno.infra.smart.scene.entity.ArticleManagerEntity;
import com.alinesno.infra.smart.scene.entity.ArticleTemplateEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 试卷场景服务实现类
 */
@Slf4j
@Service
public class ArticleManagerServiceImpl extends IBaseServiceImpl<ArticleManagerEntity, ArticleManagerMapper> implements IArticleManagerService {

    @Autowired
    private ArticleTemplateMapper articleTemplateMapper;

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

//    @Override
//    public List<ArticleManagerResponseDto> pagerListByPage(DatatablesPageBean page,Long sceneId , PermissionQuery query) {
//
//        page.setPageNum(0);
//        page.setPageSize(20);
//
//        Page<ArticleManagerEntity> pageBean = new Page<>(page.getPageNum(), page.getPageSize());
//
//        LambdaQueryWrapper<ArticleManagerEntity> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(ArticleManagerEntity::getOrgId, query.getOrgId()) ;
//        wrapper.eq(ArticleManagerEntity::getSceneId, sceneId);
//        wrapper.orderByDesc(ArticleManagerEntity::getAddTime) ;
//
//        pageBean = page(pageBean, wrapper);
//
//        List<ArticleManagerEntity> list =  pageBean.getRecords();
//        // 查找出所有的templateId，然后查找出所有的templateName
//        if(list != null){
//            List<String> templateIds = new ArrayList<>();
//            for(ArticleManagerEntity entity : list){
//                templateIds.add(entity.getTemplateId());
//            }
//
//            List<ArticleTemplateEntity> templateList = null ;
//            if(!templateIds.isEmpty()){
//                templateList = articleTemplateMapper.selectList(
//                        new LambdaQueryWrapper<ArticleTemplateEntity>()
//                                .in(ArticleTemplateEntity::getId, templateIds));
//            }
//
//            List<ArticleManagerResponseDto> dtoList = new ArrayList<>() ;
//            for(ArticleManagerEntity entity : list){
//                ArticleManagerResponseDto dto = new ArticleManagerResponseDto() ;
//                BeanUtils.copyProperties(entity, dto);
//
//                if(StringUtils.isEmpty(entity.getTemplateId())){
//                    if(StringUtils.isNotEmpty(entity.getTemplateId())){
//                        for(ArticleTemplateEntity templateEntity : templateList){
//                            if(templateEntity.getId() ==  Long.parseLong(dto.getTemplateId())){
//                                dto.setTemplateName(templateEntity.getName());
//                            }
//                        }
//                    }else{
//                        dto.setTemplateName("未选择");
//                    }
//                }else{
//                    dto.setTemplateName("未选择");
//                }
//
//                dtoList.add(dto);
//            }
//
//            return dtoList ;
//        }
//
//        return Collections.emptyList();
//    }

    @Override
    public List<ArticleManagerResponseDto> pagerListByPage(DatatablesPageBean page, Long sceneId, PermissionQuery query) {
        // 设置分页参数
        page.setPageNum(0);
        page.setPageSize(20);

        // 构建查询条件
        LambdaQueryWrapper<ArticleManagerEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleManagerEntity::getOrgId, query.getOrgId())
                .eq(ArticleManagerEntity::getSceneId, sceneId)
                .orderByDesc(ArticleManagerEntity::getAddTime);

        // 执行分页查询
        Page<ArticleManagerEntity> pageBean = page(new Page<>(page.getPageNum(), page.getPageSize()), wrapper);
        List<ArticleManagerEntity> list = pageBean.getRecords();

        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        // 批量获取模板信息
        Map<String, String> templateNameMap = getTemplateNames(list);

        // 转换为DTO列表
        return list.stream()
                .map(entity -> convertToDto(entity, templateNameMap))
                .toList();
    }

    /**
     * 批量获取模板名称映射
     */
    private Map<String, String> getTemplateNames(List<ArticleManagerEntity> articles) {
        // 收集非空模板ID
        List<String> templateIds = articles.stream()
                .map(ArticleManagerEntity::getTemplateId)
                .filter(StringUtils::isNotEmpty)
                .distinct()
                .toList();

        if (templateIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // 批量查询模板
        List<ArticleTemplateEntity> templateList = articleTemplateMapper.selectList(
                new LambdaQueryWrapper<ArticleTemplateEntity>()
                        .in(ArticleTemplateEntity::getId, templateIds));

        // 构建模板ID到名称的映射
        return templateList.stream()
                .collect(Collectors.toMap(
                        template -> template.getId().toString(),
                        ArticleTemplateEntity::getName,
                        (existing, replacement) -> existing));
    }

    /**
     * 转换为DTO对象
     */
    private ArticleManagerResponseDto convertToDto(ArticleManagerEntity entity, Map<String, String> templateNameMap) {
        ArticleManagerResponseDto dto = new ArticleManagerResponseDto();
        BeanUtils.copyProperties(entity, dto);

        // 设置模板名称
        if (StringUtils.isNotEmpty(entity.getTemplateId())) {
            dto.setTemplateName(templateNameMap.getOrDefault(entity.getTemplateId(), "未选择"));
        } else {
            dto.setTemplateName("未选择");
        }

        return dto;
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
