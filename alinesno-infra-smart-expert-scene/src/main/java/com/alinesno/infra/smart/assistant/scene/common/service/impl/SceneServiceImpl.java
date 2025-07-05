package com.alinesno.infra.smart.assistant.scene.common.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.enums.HasDeleteEnums;
import com.alinesno.infra.common.web.adapter.base.consumer.IBaseOrganizationConsumer;
import com.alinesno.infra.common.web.adapter.base.dto.OrganizationDto;
import com.alinesno.infra.common.web.log.utils.SpringUtils;
import com.alinesno.infra.smart.assistant.adapter.service.BaseSearchConsumer;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.scene.common.mapper.SceneMapper;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.service.IArticleWriterSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.IContentFormatterSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.service.IDeepSearchSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.documentReader.service.IDocReaderSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.service.IExamPagerSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.generalAgent.service.IGeneralAgentSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.service.IChapterService;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.service.ILongTextSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.service.IProjectResearchSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.prototypeDesign.service.IPrototypeSceneService;
import com.alinesno.infra.smart.im.enums.ChannelType;
import com.alinesno.infra.smart.scene.dto.*;
import com.alinesno.infra.smart.scene.entity.ChapterEntity;
import com.alinesno.infra.smart.scene.entity.SceneEntity;
import com.alinesno.infra.smart.scene.enums.SceneEnum;
import com.alinesno.infra.smart.scene.service.ISceneService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SceneServiceImpl extends IBaseServiceImpl<SceneEntity, SceneMapper> implements ISceneService {

    @Autowired
    private BaseSearchConsumer baseSearchConsumer; ;

    @Autowired
    private IChapterService chapterService ;

    @Autowired
    private IBaseOrganizationConsumer organizationConsumer ;

    @Override
    public SceneEntity saveScene(SceneDto sceneDto) {

        SceneEntity sceneEntity = new SceneEntity();
        BeanUtils.copyProperties(sceneDto, sceneEntity);

//        // 创建频道知识库
//        R<Long> createDataset = baseSearchConsumer.datasetCreate(
//                sceneEntity.getSceneDesc() ,
//                sceneEntity.getSceneName() ,
//                sceneDto.getOrgId() + "" ,
//                sceneDto.getOperatorId() + "") ;
//        sceneEntity.setKnowledgeId(createDataset.getData()+"");

        save(sceneEntity);

        return sceneEntity ;
    }

    public String genMarkdownContent(long sceneId, PermissionQuery query, Long longTextSceneId, Long taskId) {

        LambdaQueryWrapper<ChapterEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChapterEntity::getSceneId, sceneId);
        lambdaQueryWrapper.eq(ChapterEntity::getTaskId, taskId);
        lambdaQueryWrapper.eq(ChapterEntity::getOrgId, query.getOrgId()) ;
        lambdaQueryWrapper.eq(ChapterEntity::getLongTextSceneId, longTextSceneId) ;
        lambdaQueryWrapper.orderByAsc(ChapterEntity::getChapterSort); // 按照排序字段升序排列

        List<ChapterEntity> allChapters = chapterService.list(lambdaQueryWrapper);

        // 构建树结构
        Map<Long, ChapterEntity> chapterMap = new HashMap<>();
        List<ChapterEntity> topChapters = new ArrayList<>();

        for (ChapterEntity chapter : allChapters) {
            chapterMap.put(chapter.getId(), chapter);
        }

        for (ChapterEntity chapter : allChapters) {
            Long parentId = chapter.getParentChapterId();
            if (parentId == null) {
                topChapters.add(chapter);
            } else {
                ChapterEntity parent = chapterMap.get(parentId);
                if (parent != null) {
                    if (parent.getSubtitles() == null) {
                        parent.setSubtitles(new ArrayList<>());
                    }
                    parent.getSubtitles().add(chapter);
                }
            }
        }

        StringBuilder markdownBuilder = new StringBuilder();

        for (ChapterEntity chapter : topChapters) {
            buildMarkdownForChapter(chapter, markdownBuilder, 1);
        }

        return markdownBuilder.toString();

    }

    private void buildMarkdownForChapter(ChapterEntity chapter, StringBuilder markdownBuilder, int level) {
        // 根据层级添加标题符号
        markdownBuilder.append("#".repeat(Math.max(0, level)));
        markdownBuilder.append(" ").append(chapter.getChapterName()).append("\n\n");
        if (chapter.getContent() != null) {
            markdownBuilder.append(chapter.getContent()).append("\n\n");
        }

        if (chapter.getSubtitles() != null) {
            for (ChapterEntity subChapter : chapter.getSubtitles()) {
                buildMarkdownForChapter(subChapter, markdownBuilder, level + 1);
            }
        }
    }

    @Override
    public IPage<SceneResponseDto> sceneListByPage(PermissionQuery query, SceneQueryDto pageDto) {

        long orgId = query.getOrgId(); // 组织 id
        long operatorId = query.getOperatorId();  // 创建人员(私有频道)

        String publicChannelType = ChannelType.PUBLIC_CHANNEL.getValue() ;
        String privateChannelType = ChannelType.PRIVATE_CHANNEL.getValue() ;

        LambdaQueryWrapper<SceneEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SceneEntity::getHasDelete, HasDeleteEnums.LEGAL.value);

        // 查询当前组织频道、公共频道、我的私有频道
        queryWrapper.and(wrapper -> wrapper
                .eq(SceneEntity::getOrgId, orgId)
                .or()
                .eq(SceneEntity::getSceneScope, publicChannelType)
                .or(w -> w
                        .eq(SceneEntity::getSceneScope, privateChannelType)
                        .eq(SceneEntity::getOperatorId, operatorId)
                ));

        if(pageDto.getSceneType() != null){
            queryWrapper.eq(SceneEntity::getSceneType, pageDto.getSceneType());
        }

        if(pageDto.getSceneName() != null){
            queryWrapper.like(SceneEntity::getSceneName, pageDto.getSceneName());
        }

        queryWrapper.orderByDesc(SceneEntity::getAddTime);

        IPage<SceneEntity> page = new Page<>(pageDto.getPageNum(), pageDto.getPageSize());
        page =  this.page(page, queryWrapper);

        // 获取到orgId
        List<Long> orgIds = page.getRecords().stream()
                .map(SceneEntity::getOrgId)
                .distinct()
                .toList();

        log.debug("orgIds = {}" , orgIds);

        List<OrganizationDto> orgList = organizationConsumer.findOrgByIds(orgIds).getData();

        return page.convert(entity -> {

            SceneResponseDto dto = new SceneResponseDto() ;
            BeanUtils.copyProperties(entity, dto);

            if(orgList != null){
                dto.setOrgName(orgList.stream()
                        .filter(org -> org.getId().equals(entity.getOrgId()))
                        .findFirst()
                        .map(OrganizationDto::getOrgName)
                        .orElse("未知组织"));
            }

            return dto ;
        });
    }

    @Override
    public List<SceneEntity> allPublicScene() {
        LambdaQueryWrapper<SceneEntity> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(SceneEntity::getSceneScope, ChannelType.PUBLIC_CHANNEL.getValue());
        queryWrapper.eq(SceneEntity::getHasDelete, HasDeleteEnums.LEGAL.value);
        queryWrapper.orderByDesc(SceneEntity::getAddTime);

        return this.list(queryWrapper);
    }

    /**
     * 更新场景代理
     * @param dto
     */
    @Override
    public void updateSceneAgents(UpdateSceneAgentDto dto) {
        String sceneTypeCode = dto.getSceneTypeCode() ;

        // 文本审核
        if(sceneTypeCode.equals(SceneEnum.LONG_TEXT.getSceneInfo().getCode())){
            ILongTextSceneService longTextSceneService =  SpringUtils.getBean(ILongTextSceneService.class) ;
            longTextSceneService.updateSceneAgents(dto);
        }
        // 文档审核
        else if (sceneTypeCode.equals(SceneEnum.DOCUMENT_REVIEW.getSceneInfo().getCode())) {
            IDocReviewSceneService docReviewSceneService = SpringUtils.getBean(IDocReviewSceneService.class) ;
            docReviewSceneService.updateSceneAgents(dto);
        }
        // 文本阅读
        else if(sceneTypeCode.equals(SceneEnum.DOCUMENT_READER.getSceneInfo().getCode())){
            IDocReaderSceneService documentReaderSceneService = SpringUtils.getBean(IDocReaderSceneService.class) ;
            documentReaderSceneService.updateSceneAgents(dto);
        }
        // 内容排版
        else if(sceneTypeCode.equals(SceneEnum.CONTENT_FORMATTER.getSceneInfo().getCode())){
            IContentFormatterSceneService contentFormatterSceneService = SpringUtils.getBean(IContentFormatterSceneService.class) ;
            contentFormatterSceneService.updateSceneAgents(dto);
        }
        // 通用智能体
        else if(sceneTypeCode.equals(SceneEnum.GENERAL_AGENT.getSceneInfo().getCode())){
            IGeneralAgentSceneService generalAgentSceneService = SpringUtils.getBean(IGeneralAgentSceneService.class) ;
            generalAgentSceneService.updateSceneAgents(dto);
        }
        // 深度检索
        else if(sceneTypeCode.equals(SceneEnum.DEEP_SEARCH.getSceneInfo().getCode())){
            IDeepSearchSceneService deepSearchSceneService = SpringUtils.getBean(IDeepSearchSceneService.class) ;
            deepSearchSceneService.updateSceneAgents(dto);
        }
        // 培训考试
        else if(sceneTypeCode.equals(SceneEnum.EXAM_AGENT.getSceneInfo().getCode())){
            IExamPagerSceneService examPagerSceneService = SpringUtils.getBean(IExamPagerSceneService.class) ;
            examPagerSceneService.updateSceneAgents(dto);
        }
//        // PPT生成
//        else if(sceneTypeCode.equals(SceneEnum.PPT_CREATION.getSceneInfo().getCode())){
//            IPPTGeneratorSceneService pptGeneratorSceneService = SpringUtils.getBean(IPPTGeneratorSceneService.class) ;
//            pptGeneratorSceneService.updateSceneAgents(dto);
//        }
        // 文章编写
        else if(sceneTypeCode.equals(SceneEnum.ARTICLE_WRITING.getSceneInfo().getCode())){
            IArticleWriterSceneService articleWriterSceneService = SpringUtils.getBean(IArticleWriterSceneService.class) ;
            articleWriterSceneService.updateSceneAgents(dto);
        }
        // 项目检索
         else if(sceneTypeCode.equals(SceneEnum.PRODUCT_RESEARCH.getSceneInfo().getCode())){
            IProjectResearchSceneService productResearchSceneService = SpringUtils.getBean(IProjectResearchSceneService.class) ;
              productResearchSceneService.updateSceneAgents(dto);
        }
         // 原型设计
         else if(sceneTypeCode.equals(SceneEnum.PROTOTYPE_DESIGN.getSceneInfo().getCode())){
            IPrototypeSceneService prototypeDesignSceneService = SpringUtils.getBean(IPrototypeSceneService.class) ;
               prototypeDesignSceneService.updateSceneAgents(dto);
        }

    }

    /**
     * 获取角色列表
     * @param dto
     * @return
     */
    @Override
    public List<IndustryRoleEntity> getRoleList(RoleListRequestDto dto) {
        String sceneTypeCode = dto.getSceneTypeCode() ;

        // 长文本
        if(sceneTypeCode.equals(SceneEnum.LONG_TEXT.getSceneInfo().getCode())){
            ILongTextSceneService longTextSceneService =  SpringUtils.getBean(ILongTextSceneService.class) ;
            return longTextSceneService.getRoleList(dto);
        }
        // 文档审核
        else if (sceneTypeCode.equals(SceneEnum.DOCUMENT_REVIEW.getSceneInfo().getCode())) {
            IDocReviewSceneService docReviewSceneService = SpringUtils.getBean(IDocReviewSceneService.class) ;
            return docReviewSceneService.getRoleList(dto);
        }
        // 文本阅读
        else if(sceneTypeCode.equals(SceneEnum.DOCUMENT_READER.getSceneInfo().getCode())){
            IDocReaderSceneService documentReaderSceneService = SpringUtils.getBean(IDocReaderSceneService.class) ;
            return documentReaderSceneService.getRoleList(dto);
        }
        // 内容排版
        else if(sceneTypeCode.equals(SceneEnum.CONTENT_FORMATTER.getSceneInfo().getCode())){
            IContentFormatterSceneService contentFormatterSceneService = SpringUtils.getBean(IContentFormatterSceneService.class) ;
            return contentFormatterSceneService.getRoleList(dto);
        }
        // 通用智能体
        else if(sceneTypeCode.equals(SceneEnum.GENERAL_AGENT.getSceneInfo().getCode())){
            IGeneralAgentSceneService generalAgentSceneService = SpringUtils.getBean(IGeneralAgentSceneService.class) ;
            return generalAgentSceneService.getRoleList(dto);
        }
        // 深度检索
        else if(sceneTypeCode.equals(SceneEnum.DEEP_SEARCH.getSceneInfo().getCode())){
            IDeepSearchSceneService deepSearchSceneService = SpringUtils.getBean(IDeepSearchSceneService.class) ;
            return deepSearchSceneService.getRoleList(dto);
        }
        // 培训考试
        else if(sceneTypeCode.equals(SceneEnum.EXAM_AGENT.getSceneInfo().getCode())){
            IExamPagerSceneService examPagerSceneService = SpringUtils.getBean(IExamPagerSceneService.class) ;
            return examPagerSceneService.getRoleList(dto);
        }
//        // PPT生成
//        else if(sceneTypeCode.equals(SceneEnum.PPT_CREATION.getSceneInfo().getCode())){
//            IPPTGeneratorSceneService pptGeneratorSceneService = SpringUtils.getBean(IPPTGeneratorSceneService.class) ;
//            return pptGeneratorSceneService.getRoleList(dto);
//        }
        // 文章编写
        else if(sceneTypeCode.equals(SceneEnum.ARTICLE_WRITING.getSceneInfo().getCode())){
            IArticleWriterSceneService articleWriterSceneService = SpringUtils.getBean(IArticleWriterSceneService.class) ;
            return articleWriterSceneService.getRoleList(dto);
        }
        // 项目检索
        else if(sceneTypeCode.equals(SceneEnum.PRODUCT_RESEARCH.getSceneInfo().getCode())){
             IProjectResearchSceneService projectResearchSceneService = SpringUtils.getBean(IProjectResearchSceneService.class) ;
             return projectResearchSceneService.getRoleList(dto);
        }
        // 原型设计
         else if(sceneTypeCode.equals(SceneEnum.PROTOTYPE_DESIGN.getSceneInfo().getCode())){
            IPrototypeSceneService prototypeDesignSceneService = SpringUtils.getBean(IPrototypeSceneService.class) ;
              return prototypeDesignSceneService.getRoleList(dto);
        }

        return null;
    }

}
