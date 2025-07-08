package com.alinesno.infra.smart.assistant.scene.scene.examPaper.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ExamMarkingDto;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ExamPageSubmitDto;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.enums.ExamineeExamEnums;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.mapper.ExamInfoMapper;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.mapper.ExamPagerMapper;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.mapper.ExamScoreMapper;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.mapper.ExamineeMapper;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.service.IExamScoreService;
import com.alinesno.infra.smart.scene.entity.ExamInfoEntity;
import com.alinesno.infra.smart.scene.entity.ExamPagerEntity;
import com.alinesno.infra.smart.scene.entity.ExamScoreEntity;
import com.alinesno.infra.smart.scene.entity.ExamineeEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;

/**
 * 试卷场景服务实现类
 */
@Slf4j
@Service
public class ExamScoreServiceImpl extends IBaseServiceImpl<ExamScoreEntity, ExamScoreMapper> implements IExamScoreService {

    @Autowired
    private ExamInfoMapper examInfoMapper ; // 考试信息Mapper层

    @Autowired
    private ExamPagerMapper examPagerMapper ;  // 试卷Mapper层

    @Autowired
    private ExamineeMapper examineeMapper ; // 考生Mapper层

    @Override
    public Long saveAccountScore(ExamPageSubmitDto dto) {

        String examId = dto.getExamId() ;
        ExamInfoEntity examInfoEntity = examInfoMapper.selectById(examId) ;  // 获取到考试信息

        Long sceneId = examInfoEntity.getSceneId() ;
        Long pageId = examInfoEntity.getPagerId() ;
        ExamPagerEntity examPagerEntity =  examPagerMapper.selectById(pageId) ;

        String accountId = dto.getAccountId() ; // 获取到考生信息
        ExamineeEntity examineeEntity = examineeMapper.selectById(accountId)  ; // 获取到考生信息

        // 判断是否前面已经提交过试卷，同一个考生不能在同一张试卷上提交两次
        LambdaQueryWrapper<ExamScoreEntity> waqWrapper = new LambdaQueryWrapper<>() ;
        waqWrapper.eq(ExamScoreEntity::getExamInfoId, examId) ;
        waqWrapper.eq(ExamScoreEntity::getUserId, accountId) ;

        if(count(waqWrapper) > 0){
            log.error("该考生已经提交过试卷，请勿重复提交！");

            ExamScoreEntity examScoreEntity = getOne(waqWrapper) ;
            return examScoreEntity.getId() ;
        }

        String submitTime = dto.getSubmitTime() ; // 获取到提交时间

        String answers = dto.getAnswers().toJSONString() ; // 获取到考生答案
        String questions = dto.getQuestions().toJSONString() ; // 获取到试题信息

        ExamScoreEntity examScoreEntity = new ExamScoreEntity() ;

        examScoreEntity.setPagerId(pageId) ;
        examScoreEntity.setSceneId(sceneId) ;
        examScoreEntity.setExamInfoId(Long.valueOf(examId)) ;
        examScoreEntity.setUserId(Long.valueOf(accountId)) ;
        examScoreEntity.setUserName(examineeEntity.getName()) ;
        examScoreEntity.setUserCode(examineeEntity.getExamineeId()) ;
        examScoreEntity.setSubmitTime(Instant.parse(submitTime).atZone(ZoneId.systemDefault()).toLocalDateTime());

        examScoreEntity.setIsDeleted(0);
        examScoreEntity.setAnswers(answers) ;
        examScoreEntity.setQuestions(questions) ;
        examScoreEntity.setExamStatus(ExamineeExamEnums.EXAMINATION_END.getCode());

        // 设置权限相关
        examScoreEntity.setOrgId(examInfoEntity.getOrgId());
        examScoreEntity.setDepartmentId(examInfoEntity.getDepartmentId());
        examScoreEntity.setOperatorId(examInfoEntity.getOperatorId());

        save(examScoreEntity);

        return examScoreEntity.getId();
    }

    /**
     * 保存阅卷结果
     *
     * @param examMarkingDto
     * @param currentAccountId
     */
    @Override
    public void saveMarkingResults(ExamMarkingDto examMarkingDto, Long currentAccountId , String accountName) {

        // 通过考试号和学生id获取到考试信息
        LambdaQueryWrapper<ExamScoreEntity> waqWrapper = new LambdaQueryWrapper<>() ;
        waqWrapper.eq(ExamScoreEntity::getExamInfoId, examMarkingDto.getExamId()) ;
        waqWrapper.eq(ExamScoreEntity::getUserId , examMarkingDto.getExamineeId()) ;
        waqWrapper.eq(ExamScoreEntity::getSceneId, examMarkingDto.getSceneId()) ;

        ExamScoreEntity examScoreEntity = getOne(waqWrapper) ;
        examScoreEntity.setScore(examMarkingDto.getTotalScore()) ; // 设置总分

        examScoreEntity.setReviewerId(currentAccountId) ; // 阅卷人
        examScoreEntity.setReviewerName(accountName) ;  // 阅卷人姓名

        examScoreEntity.setReviewResult(JSONArray.toJSONString(examMarkingDto.getQuestionScores())) ; // 阅卷结果{key:value}保存，key是题目id,value是分数

        examScoreEntity.setExamStatus(ExamineeExamEnums.REVIEW_END.getCode()) ;

        updateById(examScoreEntity) ;

    }
}
