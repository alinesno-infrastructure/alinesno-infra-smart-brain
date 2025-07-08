package com.alinesno.infra.smart.assistant.scene.scene.examPaper.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ExamMarkingDto;
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
     *
     * @param dto
     * @return
     */
    Long saveAccountScore(ExamPageSubmitDto dto);

    /**
     * 保存阅卷结果
     *
     * @param examMarkingDto
     * @param currentAccountId
     */
    void saveMarkingResults(ExamMarkingDto examMarkingDto, Long currentAccountId , String accountName);
}
