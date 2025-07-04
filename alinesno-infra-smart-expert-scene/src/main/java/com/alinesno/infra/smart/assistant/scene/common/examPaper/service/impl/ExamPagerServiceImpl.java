package com.alinesno.infra.smart.assistant.scene.common.examPaper.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.smart.assistant.scene.common.examPaper.dto.ExamPaperDTO;
import com.alinesno.infra.smart.assistant.scene.common.examPaper.dto.ExamPaperUpdateDTO;
import com.alinesno.infra.smart.assistant.scene.common.examPaper.dto.QuestionDTO;
import com.alinesno.infra.smart.assistant.scene.common.examPaper.mapper.ExamPagerMapper;
import com.alinesno.infra.smart.assistant.scene.common.examPaper.service.IExamPagerService;
import com.alinesno.infra.smart.assistant.scene.common.examPaper.service.IExamQuestionBankService;
import com.alinesno.infra.smart.assistant.scene.common.examPaper.service.IExamQuestionService;
import com.alinesno.infra.smart.scene.entity.ExamPagerEntity;
import com.alinesno.infra.smart.scene.entity.ExamQuestionBankEntity;
import com.alinesno.infra.smart.scene.entity.ExamQuestionEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 试卷场景服务实现类
 */
@Slf4j
@Service
public class ExamPagerServiceImpl extends IBaseServiceImpl<ExamPagerEntity , ExamPagerMapper> implements IExamPagerService {

    @Autowired
    private IExamQuestionService examQuestionService ;

    @Autowired
    private IExamQuestionBankService examQuestionBankService;


    @Override
    public ExamPaperDTO getPagerDetail(Long id) {

        ExamPaperDTO dto = new ExamPaperDTO() ;

        // 获取到试卷信息
        ExamPagerEntity entity = getById(id) ;

        dto.setId(id);
        dto.setPagerName(entity.getTitle());
        dto.setDifficulty(entity.getDifficulty());
        dto.setPagerDesc(entity.getDescription());
        dto.setPagerType("pager");

        // 获取到试卷的题目信息
        LambdaQueryWrapper<ExamQuestionEntity> waqWrapper = new LambdaQueryWrapper<>();
        waqWrapper.eq(ExamQuestionEntity::getPagerId, id);

        List<ExamQuestionEntity> questionList = examQuestionService.list(waqWrapper);

        List<QuestionDTO> dtoList = questionList.stream().map(question -> {
            QuestionDTO dtoInner = new QuestionDTO() ;

            CopyOptions copyOptions = CopyOptions.create()
                    .ignoreNullValue()
                    .ignoreError()
                    .ignoreCase();
            BeanUtil.copyProperties(question , dtoInner , copyOptions);

            if(StringUtils.isNotNull(question.getAnswers())){
                dtoInner.setAnswers(JSONObject.parseArray(question.getAnswers() , JSONObject.class));
            }else{
                dtoInner.setAnswers(Collections.emptyList());
            }

            if(StringUtils.isNotNull(question.getBlanks())){
                dtoInner.setBlanks(JSONObject.parseArray(question.getBlanks() , JSONObject.class));
            }else{
                dtoInner.setBlanks(Collections.emptyList());
            }

            dtoInner.setOrder(question.getSortOrder());
            dtoInner.setId(String.valueOf(question.getId()));

            return dtoInner;
        }).toList();

        dto.setQuestionList(dtoList);

        return dto;
    }

    /**
     * 保存试卷
     *
     * @param dto
     * @return
     */
    private Long savePagerQuestion(ExamPaperDTO dto) {

        String pagerName = dto.getPagerName() ;

        ExamPagerEntity pagerEntity = new ExamPagerEntity();
        BeanUtils.copyProperties(dto , pagerEntity);

        pagerEntity.setTitle(pagerName);
        pagerEntity.setDescription(dto.getPagerDesc());
        pagerEntity.setDifficulty(dto.getDifficulty());
        pagerEntity.setSceneId(dto.getSceneId());

        save(pagerEntity);

        List<ExamQuestionEntity> questionList = new ArrayList<>() ;
        List<QuestionDTO> list = dto.getQuestionList() ;

        for(QuestionDTO questionDTO : list){
            ExamQuestionEntity e = getExamQuestionEntity(dto, questionDTO);
            e.setPagerId(pagerEntity.getId());
            questionList.add(e);
        }

        examQuestionService.saveBatch(questionList);

        return pagerEntity.getId() ;
    }

    @Override
    public Long savePager(ExamPaperDTO dto) {
        String pagerType = dto.getPagerType() ;
        if("pager".equals(pagerType)){  // 保存到试卷
            return savePagerQuestion(dto);
        }else if("banks".equals(pagerType)){  // 保存到题库
            return savePagerBanks(dto);
        }
        return null ;
    }

    /**
     * 更新试卷信息
     * @param dto
     */
    @Override
    public void updatePager(ExamPaperUpdateDTO dto) {
        log.debug("dto = {}" , dto);

        String pagerName = dto.getPagerName() ;

        ExamPagerEntity pagerEntity = new ExamPagerEntity();
        BeanUtils.copyProperties(dto , pagerEntity);

        pagerEntity.setTitle(pagerName);
        pagerEntity.setDescription(dto.getPagerDesc());
        pagerEntity.setDifficulty(dto.getDifficulty());

        update(pagerEntity);

        List<ExamQuestionEntity> questionList = new ArrayList<>() ;
        List<QuestionDTO> list = dto.getQuestionList() ;

        ExamPaperDTO examPaperDTO = new ExamPaperDTO() ;
        BeanUtil.copyProperties(dto , examPaperDTO);

        for(QuestionDTO questionDTO : list){
            ExamQuestionEntity e = getExamQuestionEntity(examPaperDTO, questionDTO);
            e.setId(Long.valueOf(questionDTO.getId()));
            e.setPagerId(pagerEntity.getId());
            questionList.add(e);
        }

        examQuestionService.saveOrUpdateBatch(questionList);
    }


    @Override
    public List<ExamPagerEntity> pagerListByPage(DatatablesPageBean page, PermissionQuery query, String sceneId) {

        page.setPageNum(0);
        page.setPageSize(20);

        Page<ExamPagerEntity> pageBean = new Page<>(page.getPageNum(), page.getPageSize());

        LambdaQueryWrapper<ExamPagerEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamPagerEntity::getOrgId, query.getOrgId()) ;
        wrapper.eq(ExamPagerEntity::getSceneId, sceneId) ;
        wrapper.orderByDesc(ExamPagerEntity::getAddTime) ;

        pageBean = page(pageBean, wrapper);

        return pageBean.getRecords();
    }

    @NotNull
    private static ExamQuestionEntity getExamQuestionEntity(ExamPaperDTO dto, QuestionDTO questionDTO) {
        ExamQuestionEntity e = new ExamQuestionEntity() ;

        e.setOrgId(dto.getOrgId());
        e.setOperatorId(dto.getOperatorId());
        e.setDepartmentId(dto.getDepartmentId());

        e.setAssessmentContent(questionDTO.getAssessmentContent());
        e.setIsRequired(questionDTO.getIsRequired());
        e.setScore(questionDTO.getScore());
        e.setQuestion(questionDTO.getQuestion());
        e.setAnswerAnalysis(questionDTO.getAnswerAnalysis());
        e.setType(questionDTO.getType());
        e.setSortOrder(questionDTO.getOrder());

        if(StringUtils.isNotNull(questionDTO.getAnswers())){
            e.setAnswers(JSONObject.toJSONString(questionDTO.getAnswers()));
        }
        if(StringUtils.isNotNull(questionDTO.getBlanks())){
            e.setBlanks(JSONObject.toJSONString(questionDTO.getBlanks()));
        }
        e.setSortOrder(questionDTO.getOrder());
        return e;
    }

    /**
     * 保存题库
     * @param dto
     */
    private Long savePagerBanks(ExamPaperDTO dto) {

        String pagerName = dto.getPagerName() ;

        ExamQuestionBankEntity examQuestionBankEntity = new ExamQuestionBankEntity();
        examQuestionBankEntity.setBankName(pagerName);
        examQuestionBankEntity.setSceneId(dto.getSceneId());

        examQuestionBankService.save(examQuestionBankEntity);

        List<ExamQuestionEntity> questionList = new ArrayList<>() ;

        List<QuestionDTO> list = dto.getQuestionList() ;

        for(QuestionDTO questionDTO : list){
            ExamQuestionEntity e = getExamQuestionEntity(dto, questionDTO);
            e.setPagerId(examQuestionBankEntity.getId());
            questionList.add(e);
        }

        examQuestionService.saveBatch(questionList);

        return examQuestionBankEntity.getId() ;
    }

}
