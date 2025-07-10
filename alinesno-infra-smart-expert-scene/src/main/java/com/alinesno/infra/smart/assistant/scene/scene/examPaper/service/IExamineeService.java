package com.alinesno.infra.smart.assistant.scene.scene.examPaper.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ExamSubmissionDto;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ExamineeImportDTO;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ExamineeImportParam;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ImportResult;
import com.alinesno.infra.smart.scene.entity.ExamineeEntity;
import jakarta.validation.Valid;

import java.util.List;

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

    /**
     * 导入考试人员
     *
     * @param importList
     * @param sceneId
     * @param importParam
     * @return
     */
    ImportResult importExaminees(List<ExamineeImportDTO> importList, Long sceneId, @Valid ExamineeImportParam importParam);
}
