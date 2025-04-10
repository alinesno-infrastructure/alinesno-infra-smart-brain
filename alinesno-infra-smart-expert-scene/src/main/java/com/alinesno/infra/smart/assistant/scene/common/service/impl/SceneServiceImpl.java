package com.alinesno.infra.smart.assistant.scene.common.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.enums.HasDeleteEnums;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.common.web.adapter.base.consumer.IBaseOrganizationConsumer;
import com.alinesno.infra.common.web.adapter.base.dto.OrganizationDto;
import com.alinesno.infra.common.web.log.utils.SpringUtils;
import com.alinesno.infra.smart.assistant.adapter.service.BaseSearchConsumer;
import com.alinesno.infra.smart.assistant.scene.common.mapper.SceneMapper;
import com.alinesno.infra.smart.assistant.scene.scene.documentReader.service.IDocReaderSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.dto.InitAgentsDto;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.service.IChapterService;
import com.alinesno.infra.smart.im.enums.ChannelType;
import com.alinesno.infra.smart.scene.dto.SceneDto;
import com.alinesno.infra.smart.scene.dto.SceneResponseDto;
import com.alinesno.infra.smart.scene.dto.UpdateSceneAgentDto;
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

import java.util.List;

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

        // 创建频道知识库
        R<Long> createDataset = baseSearchConsumer.datasetCreate(
                sceneEntity.getSceneDesc() ,
                sceneEntity.getSceneName() ,
                sceneDto.getOrgId() + "" ,
                sceneDto.getOperatorId() + "") ;

        sceneEntity.setKnowledgeId(createDataset.getData()+"");
        save(sceneEntity);

        return sceneEntity ;
    }

    @Override
    public String genMarkdownContent(long sceneId) {
        LambdaQueryWrapper<ChapterEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChapterEntity::getSceneId, sceneId);
        lambdaQueryWrapper.orderByAsc(ChapterEntity::getChapterSort); // 按照排序字段升序排列

        List<ChapterEntity> topChapters = chapterService.list(lambdaQueryWrapper);

        StringBuilder markdownBuilder = new StringBuilder();

        for (ChapterEntity chapter : topChapters) {
            buildMarkdownForChapter(chapter, markdownBuilder);
        }

        return markdownBuilder.toString();
    }

    private void buildMarkdownForChapter(ChapterEntity chapter, StringBuilder markdownBuilder) {
        // 根据章节层级生成对应的Markdown标题前缀
        String markdownHeaderPrefix = "#".repeat(chapter.getChapterLevel());
        // 拼接章节名称
        markdownBuilder.append(markdownHeaderPrefix).append(" ").append(chapter.getChapterName()).append("\n\n");
        // 如果有内容，则添加内容
        if (chapter.getContent() != null && !chapter.getContent().isEmpty()) {
            markdownBuilder.append(chapter.getContent()).append("\n\n");
        }

        // 递归处理子章节
        List<ChapterEntity> subtitles = chapter.getSubtitles();
        if (subtitles != null && !subtitles.isEmpty()) {
            for (ChapterEntity subtitle : subtitles) {
                buildMarkdownForChapter(subtitle, markdownBuilder);
            }
        }
    }

    @Override
    public IPage<SceneResponseDto> sceneListByPage(PermissionQuery query, int pageNum, int pageSize) {

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

        queryWrapper.orderByDesc(SceneEntity::getAddTime);

        IPage<SceneEntity> page = new Page<>(pageNum, pageSize);
        page =  this.page(page, queryWrapper);

        // 获取到orgId
        List<Long> orgIds = page.getRecords().stream()
                .map(SceneEntity::getOrgId)
                .distinct()
                .toList();

        log.debug("orgIds = {}" , orgIds);

        return page.convert(entity -> {

            SceneResponseDto dto = new SceneResponseDto() ;
            BeanUtils.copyProperties(entity, dto);

            OrganizationDto org = organizationConsumer.findOrg(entity.getOrgId()).getData();
            if(org != null){
                dto.setOrgName(org.getOrgName());
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

    @Override
    public void updateSceneAgents(UpdateSceneAgentDto dto) {
        String sceneTypeCode = dto.getSceneTypeCode() ;

        if(sceneTypeCode.equals(SceneEnum.LONG_TEXT.getSceneInfo().getCode())){
            chapterService.updateSceneAgents(dto);
        } else if (sceneTypeCode.equals(SceneEnum.DOCUMENT_REVIEW.getSceneInfo().getCode())) {
            IDocReviewSceneService docReviewSceneService = SpringUtils.getBean(IDocReviewSceneService.class) ;
            docReviewSceneService.updateSceneAgents(dto);
        }else if(sceneTypeCode.equals(SceneEnum.DOCUMENT_READER.getSceneInfo().getCode())){
            IDocReaderSceneService documentReaderSceneService = SpringUtils.getBean(IDocReaderSceneService.class) ;
            documentReaderSceneService.updateSceneAgents(dto);
        }
//        else if(sceneTypeCode.equals(SceneEnum.VIDEO_GENERATION.getSceneInfo().getCode())){
//
//        }

    }
}
