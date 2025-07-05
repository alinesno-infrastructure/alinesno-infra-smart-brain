package com.alinesno.infra.smart.assistant.scene.scene.examPaper.controller;

import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ExamPageSubmitDto;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ExamSubmissionDto;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.service.IExamInfoService;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.service.IExamPagerService;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.service.IExamScoreService;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.service.IExamineeService;
import com.alinesno.infra.smart.scene.entity.ExamInfoEntity;
import com.alinesno.infra.smart.scene.entity.ExamPagerEntity;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 处理与BusinessLogEntity相关的请求的Controller。
 * 继承自BaseController类并实现IBusinessLogService接口。
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@Slf4j
@RestController
@Scope(SpringInstanceScope.PROTOTYPE)
@RequestMapping("/api/infra/smart/assistant/scene/examPagerJob")
public class ExamPagerJobController extends BaseController<ExamPagerEntity, IExamPagerService> {

    @Autowired
    private IExamPagerService service;

    @Autowired
    private IExamInfoService examInfoService ;

    @Autowired
    private IExamineeService examineeService ;

    @Autowired
    private IExamScoreService examScoreService  ;

    /**
     * 验证考生信息
     * @return
     */
    @PostMapping("/validateAccount")
    public AjaxResult validateAccount(@RequestBody @Valid ExamSubmissionDto submission){

        log.debug("submission = {}", submission);

        // 验证试卷唯一码
        ExamInfoEntity examInfo = examInfoService.getExamInfoByUniqueCode(submission.getPaperCode() , submission.getExamId()) ;
        log.debug("examInfo = {}" , examInfo);
//        Assert.notNull(examInfo , "试卷唯一码不正确.");

        String accountId = examineeService.initExaminee(submission) ;
        log.debug("accountId = {}", accountId);

        return AjaxResult.success("操作成功." , accountId) ;
    }

    /**
     * 保存考试结果
     * @param dto
     * @return
     */
    @PostMapping("/saveAccountScore")
    public AjaxResult saveAccountScore(@RequestBody @Valid ExamPageSubmitDto dto) {

        log.debug("dto = {}" , dto);

        examScoreService.saveAccountScore(dto) ;

        return AjaxResult.success("操作成功.") ;
    }

    @Override
    public IExamPagerService getFeign() {
        return this.service;
    }
}