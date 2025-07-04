package com.alinesno.infra.smart.assistant.scene.common.examPaper.service;

import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.scene.common.examPaper.dto.ExamInfoPage;
import com.alinesno.infra.smart.scene.entity.ExamInfoEntity;

/**
 * 考试信息服务接口
 * @author luoxiaodong
 * @version 1.0.0
 */
public interface IExamInfoService extends IBaseService<ExamInfoEntity> {

    /**
     * 查询考试列表
     *
     * @param examInfoPage
     * @param query
     * @return
     */
    TableDataInfo queryExamList(ExamInfoPage examInfoPage, PermissionQuery query);

    /**
     * 获取考试信息
     * @param paperCode
     * @param examId
     * @return
     */
    ExamInfoEntity getExamInfoByUniqueCode(String paperCode, String examId);
}
