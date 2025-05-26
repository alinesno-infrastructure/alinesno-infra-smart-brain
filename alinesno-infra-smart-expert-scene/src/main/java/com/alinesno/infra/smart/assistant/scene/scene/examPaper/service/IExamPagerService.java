package com.alinesno.infra.smart.assistant.scene.scene.examPaper.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ExamPaperDTO;
import com.alinesno.infra.smart.scene.entity.ExamPagerEntity;

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

}
