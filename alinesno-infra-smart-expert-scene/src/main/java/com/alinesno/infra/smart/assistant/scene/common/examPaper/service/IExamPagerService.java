package com.alinesno.infra.smart.assistant.scene.common.examPaper.service;

import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.scene.common.examPaper.dto.ExamPaperDTO;
import com.alinesno.infra.smart.assistant.scene.common.examPaper.dto.ExamPaperUpdateDTO;
import com.alinesno.infra.smart.scene.entity.ExamPagerEntity;

import java.util.List;

/**
 *
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
public interface IExamPagerService extends IBaseService<ExamPagerEntity> {

    /**
     * 获取试卷详情
     * @param id
     * @return
     */
    ExamPaperDTO getPagerDetail(Long id);

    /**
     * 保存试卷
     * @param dto
     */
    Long savePager(ExamPaperDTO dto);

    /**
     * 更新试卷
     * @param dto
     */
    void updatePager(ExamPaperUpdateDTO dto);

    /**
     * 分页查询试卷
     *
     * @param page
     * @param query
     * @param sceneId
     * @return
     */
    List<ExamPagerEntity> pagerListByPage(DatatablesPageBean page, PermissionQuery query, String sceneId);

}
