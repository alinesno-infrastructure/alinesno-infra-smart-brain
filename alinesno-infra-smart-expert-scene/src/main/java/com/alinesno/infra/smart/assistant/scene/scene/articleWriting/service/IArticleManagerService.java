package com.alinesno.infra.smart.assistant.scene.scene.articleWriting.service;


import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.dto.ArticleGenerateSceneDto;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.dto.ArticleOutlineDto;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ExamPaperDTO;
import com.alinesno.infra.smart.scene.entity.ArticleManagerEntity;

import java.util.List;

/**
 *
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
public interface IArticleManagerService extends IBaseService<ArticleManagerEntity> {

    /**
     * 获取试卷详情
     * @param id
     * @return
     */
    ArticleGenerateSceneDto getPagerDetail(Long id);

    /**
     * 保存试卷
     * @param dto
     */
    void savePager(ExamPaperDTO dto);

    /**
     * 更新试卷
     * @param dto
     */
    void updatePager(ArticleGenerateSceneDto dto);

    /**
     * 分页查询试卷
     * @param page
     * @param query
     * @return
     */
    List<ArticleManagerEntity> pagerListByPage(DatatablesPageBean page, PermissionQuery query);

    /**
     * 保存PPT大纲
     * @param dto
     * @return
     */
    Long savePPTOutline(ArticleOutlineDto dto);
}
