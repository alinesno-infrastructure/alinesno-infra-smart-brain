package com.alinesno.infra.smart.assistant.scene.scene.examPaper.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ExamPaperDTO;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.QuestionDTO;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.mapper.ExamPagerMapper;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.service.IExamPagerService;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.service.IExamQuestionService;
import com.alinesno.infra.smart.scene.entity.ExamPagerEntity;
import com.alinesno.infra.smart.scene.entity.ExamQuestionEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 试卷场景服务实现类
 */
@Slf4j
@Service
public class ExamPagerServiceImpl extends IBaseServiceImpl<ExamPagerEntity , ExamPagerMapper> implements IExamPagerService {

    @Autowired
    private IExamQuestionService examQuestionService ;

    @Override
    public ExamPaperDTO getPagerDetail(Long id) {

        ExamPaperDTO dto = new ExamPaperDTO() ;

        // 获取到试卷信息
        ExamPagerEntity entity = getById(id) ;
        dto.setPagerName(entity.getTitle());

        // 获取到试卷的题目信息
        LambdaQueryWrapper<ExamQuestionEntity> waqWrapper = new LambdaQueryWrapper<>();
        waqWrapper.eq(ExamQuestionEntity::getPagerId, id);

        List<ExamQuestionEntity> questionList = examQuestionService.list(waqWrapper);

        List<QuestionDTO> dtoList = questionList.stream().map(question -> {
            QuestionDTO dtoInner = new QuestionDTO() ;

            BeanUtils.copyProperties(question , dtoInner);
            dtoInner.setAnswers(JSONObject.parseArray(question.getAnswers() , JSONObject.class));
            dtoInner.setBlanks(JSONObject.parseArray(question.getBlanks() , JSONObject.class));

            return dtoInner;
        }).toList();

        dto.setQuestionList(dtoList);

        return dto;
    }
}
