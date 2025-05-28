package com.alinesno.infra.smart.assistant.scene.scene.pptCreation.service;


import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ExamPaperDTO;
import com.alinesno.infra.smart.assistant.scene.scene.pptCreation.dto.PPTGenerateSceneDto;
import com.alinesno.infra.smart.assistant.scene.scene.pptCreation.dto.PPTOutlineDto;
import com.alinesno.infra.smart.scene.entity.PPTManagerEntity;

import java.util.List;

/**
 *
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
public interface IPPTManagerService extends IBaseService<PPTManagerEntity> {

    /**
     * 获取试卷详情
     * @param id
     * @return
     */
    PPTGenerateSceneDto getPagerDetail(Long id);

    /**
     * 保存试卷
     * @param dto
     */
    void savePager(ExamPaperDTO dto);

    /**
     * 更新试卷
     * @param dto
     */
    void updatePager(PPTGenerateSceneDto dto);

    /**
     * 分页查询试卷
     * @param page
     * @param query
     * @return
     */
    List<PPTManagerEntity> pagerListByPage(DatatablesPageBean page, PermissionQuery query);

    /**
     * 保存PPT大纲
     * @param dto
     * @return
     */
    Long savePPTOutline(PPTOutlineDto dto);
}
