package com.alinesno.infra.smart.assistant.scene.scene.examPaper.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ExamPageSubmitDto;
import com.alinesno.infra.smart.scene.entity.ExamScoreEntity;

/**
 * 考试成绩服务接口
 * @author luoxiaodong
 * @version 1.0.0
 */
public interface IExamScoreService extends IBaseService<ExamScoreEntity> {

    /**
     * 保存考试成绩
     * @param dto
     */
    void saveAccountScore(ExamPageSubmitDto dto);

}
