package com.alinesno.infra.smart.assistant.scene.scene.examPaper.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ExamSubmissionDto;
import com.alinesno.infra.smart.scene.entity.ExamineeEntity;

/**
 * 考试人员服务接口
 * @author luoxiaodong
 * @version 1.0.0
 */
public interface IExamineeService extends IBaseService<ExamineeEntity> {

    /**
     * 初始化考试人员
     * @param submission
     * @return
     */
    String initExaminee(ExamSubmissionDto submission);

}
