package com.alinesno.infra.smart.assistant.scene.scene.examPaper.controller;

import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ExamPageSubmitDto;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ExamScoreDto;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ExamSubmissionDto;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ExamUpdateStatusDto;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.enums.ExamineeExamEnums;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.service.*;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.tools.ExamPagerFormatMessageTool;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.scene.entity.ExamInfoEntity;
import com.alinesno.infra.smart.scene.entity.ExamPagerEntity;
import com.alinesno.infra.smart.scene.entity.ExamPagerSceneEntity;
import com.alinesno.infra.smart.scene.entity.ExamScoreEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @Autowired
    private ExamPagerFormatMessageTool examPagerFormatMessageTool ;

    @Autowired
    private IExamPagerSceneService examPagerSceneService ;

    @Autowired
    private IIndustryRoleService industryRoleService ;

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
        Long id = examScoreService.saveAccountScore(dto) ;

        return AjaxResult.success("操作成功." , String.valueOf(id)) ;
    }

    /**
     * 考试成绩分析
     * @return
     */
    @GetMapping("/examAnalysis")
    public AjaxResult examAnalysis(
            @RequestParam("examId") String examId,
            @RequestParam("examineeId") String examineeId){

        Assert.notNull(examId , "考试ID不能为空.");
        Assert.notNull(examineeId , "考生ID不能为空.");

        LambdaQueryWrapper<ExamScoreEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamScoreEntity::getExamInfoId , examId);
        wrapper.eq(ExamScoreEntity::getUserId , examineeId);

        ExamScoreEntity examScore = examScoreService.getOne(wrapper) ;
        log.debug("examScore = {}" , examScore);

        if (examScore == null) {
            return AjaxResult.error("未找到考试成绩记录");
        }

        // 还有阅卷中的状态
        if (ExamineeExamEnums.REVIEW.getCode().equals(examScore.getExamStatus())) {
            return AjaxResult.success("阅卷中，请稍后查询结果", Map.of(
                    "taskId", examScore.getId(),
                    "status", ExamineeExamEnums.REVIEW.getCode()
            ));
        }

        // 如果已经阅卷结束，直接返回结果
        if (ExamineeExamEnums.REVIEW_END.getCode().equals(examScore.getExamStatus())) {
            return AjaxResult.success("阅卷已完成", examScore);
        }

        // 只有考试结束状态的试卷，才会进入到阅卷
        if (ExamineeExamEnums.EXAMINATION_END.getCode().equals(examScore.getExamStatus())) {

            // 查询出这个场景下阅卷专员
            long sceneId = examScore.getSceneId() ;

            PermissionQuery query = new PermissionQuery() ;
            query.setOrgId(examScore.getOrgId());

            ExamPagerSceneEntity examPagerScene =  examPagerSceneService.getBySceneId(sceneId , query);
            Long answerCheckerEngineer = examPagerScene.getAnswerCheckerEngineer() ;

            IndustryRoleEntity industryRole = industryRoleService.getById(answerCheckerEngineer) ;

            // 提交到线程池异步处理
            examPagerFormatMessageTool.markExamScore(examScore , industryRole , examPagerScene);

            return AjaxResult.success("阅卷已开始，请稍后查询结果", Map.of(
                    "taskId", examScore.getId(),
                    "status", ExamineeExamEnums.REVIEW.getCode()
            ));
        }

        return AjaxResult.error("考试未结束，请稍后查询结果");
    }

    /**
     * 查询阅卷状态
     */
    @GetMapping("/checkStatus/{examId}/{examineeId}")
    public AjaxResult checkReviewStatus(@PathVariable("examId") String examId,
                                        @PathVariable("examineeId") String examineeId) {

        LambdaQueryWrapper<ExamScoreEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamScoreEntity::getExamInfoId , examId);
        wrapper.eq(ExamScoreEntity::getUserId , examineeId);

        ExamScoreEntity examScore = examScoreService.getOne(wrapper) ;
        if (examScore == null) {
            return AjaxResult.error("未找到考试记录");
        }

        // 考试ID
        Long examIdLong = examScore.getExamInfoId() ;
        ExamInfoEntity examInfo = examInfoService.getById(examIdLong) ;

        // 查询出这个场景下阅卷专员
        long sceneId = examScore.getSceneId() ;

        PermissionQuery query = new PermissionQuery() ;
        query.setOrgId(examScore.getOrgId());

        ExamPagerSceneEntity examPagerScene =  examPagerSceneService.getBySceneId(sceneId , query);
        Long answerCheckerEngineer = examPagerScene.getAnswerCheckerEngineer() ;

        IndustryRoleEntity industryRole = industryRoleService.getById(answerCheckerEngineer) ;

        return AjaxResult.success(Map.of(
                "status", examScore.getExamStatus(),
                "examInfo", examInfo,
                "completed", ExamineeExamEnums.REVIEW_END.getCode().equals(examScore.getExamStatus()),
                "answerCheckerEngineer", industryRole ,
                "result", ExamScoreDto.fromEntity(examScore)
        ));
    }

    /**
     * 更新考试状态
     * @return
     */
    @PostMapping("/updateExamStatus")
    public AjaxResult updateExamStatus(@RequestBody @Valid ExamUpdateStatusDto dto) {

        LambdaQueryWrapper<ExamScoreEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamScoreEntity::getExamInfoId , dto.getExamId());
        wrapper.eq(ExamScoreEntity::getUserId , dto.getExamineeId());

        ExamScoreEntity examScore = examScoreService.getOne(wrapper) ;
        if (examScore == null) {
            return AjaxResult.error("未找到考试记录");
        }

        examScore.setExamStatus(dto.getStatus()) ;
        examScoreService.updateById(examScore) ;

        return AjaxResult.success("操作成功");
    }

    @Override
    public IExamPagerService getFeign() {
        return this.service;
    }
}