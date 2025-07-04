package com.alinesno.infra.smart.assistant.scene.common.examPaper.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.smart.assistant.scene.common.examPaper.dto.ExamInfoPage;
import com.alinesno.infra.smart.assistant.scene.common.examPaper.mapper.ExamInfoMapper;
import com.alinesno.infra.smart.assistant.scene.common.examPaper.service.IExamInfoService;
import com.alinesno.infra.smart.scene.entity.ExamInfoEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 试卷场景服务实现类
 */
@Slf4j
@Service
public class ExamInfoServiceImpl extends IBaseServiceImpl<ExamInfoEntity, ExamInfoMapper> implements IExamInfoService {

    @Override
    public TableDataInfo queryExamList(ExamInfoPage examInfoPage, PermissionQuery query) {
        IPage<ExamInfoEntity> pageBean = Page.of(examInfoPage.getPageNum(), examInfoPage.getPageSize());

        LambdaQueryWrapper<ExamInfoEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamInfoEntity::getSceneId, examInfoPage.getSceneId()) ;
        wrapper.eq(ExamInfoEntity::getOrgId, query.getOrgId()) ;
        wrapper.orderByDesc(ExamInfoEntity::getAddTime) ;

        if(examInfoPage.getExamStatus() != null){
            wrapper.eq(ExamInfoEntity::getExamStatus, examInfoPage.getExamStatus()) ;
        }

        pageBean = this.page(pageBean, wrapper);

        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setRows(pageBean.getRecords());
        tableDataInfo.setTotal(pageBean.getTotal());

        return tableDataInfo;
    }

    @Override
    public ExamInfoEntity getExamInfoByUniqueCode(String paperCode, String examId) {
        ExamInfoEntity examInfo = getById(examId) ;

        if(examInfo != null && examInfo.getExamUniqueCode() != null && examInfo.getExamUniqueCode().equals(paperCode)) {
            return examInfo ;
        }

        return null;
    }
}
