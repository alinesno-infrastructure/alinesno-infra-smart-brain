package com.alinesno.infra.smart.assistant.scene.scene.pptCreation.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ExamPaperDTO;
import com.alinesno.infra.smart.assistant.scene.scene.pptCreation.dto.PPTGenerateSceneDto;
import com.alinesno.infra.smart.assistant.scene.scene.pptCreation.dto.PPTOutlineDto;
import com.alinesno.infra.smart.assistant.scene.scene.pptCreation.mapper.PPTManagerMapper;
import com.alinesno.infra.smart.assistant.scene.scene.pptCreation.service.IPPTManagerService;
import com.alinesno.infra.smart.scene.entity.PPTManagerEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 试卷场景服务实现类
 */
@Slf4j
@Service
public class PPTManagerServiceImpl extends IBaseServiceImpl<PPTManagerEntity , PPTManagerMapper> implements IPPTManagerService {

    @Override
    public PPTGenerateSceneDto getPagerDetail(Long id) {
        return null;
    }

    @Override
    public void savePager(ExamPaperDTO dto) {

    }

    @Override
    public void updatePager(PPTGenerateSceneDto dto) {

    }

    @Override
    public List<PPTManagerEntity> pagerListByPage(DatatablesPageBean page, PermissionQuery query) {
        
        page.setPageNum(page.getPageNum());
        page.setPageSize(page.getPageSize());

        Page<PPTManagerEntity> pageBean = new Page<>(page.getPageNum(), page.getPageSize());

        LambdaQueryWrapper<PPTManagerEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PPTManagerEntity::getOrgId, query.getOrgId()) ;
        wrapper.orderByDesc(PPTManagerEntity::getAddTime) ;

        pageBean = page(pageBean, wrapper);

        return pageBean.getRecords();
    }

    @Override
    public Long savePPTOutline(PPTOutlineDto dto) {

        Long pptId = dto.getPptId() ;

        PPTManagerEntity entity ;
        if(pptId != null){
            entity = getById(pptId)  ;
        }else {
            entity = new PPTManagerEntity() ;

            CopyOptions copyOptions = CopyOptions.create()
                    .ignoreNullValue()
                    .ignoreError()
                    .ignoreCase();
            BeanUtil.copyProperties(dto, entity , copyOptions);
        }

        entity.setSceneId(dto.getSceneId());
        entity.setTitle(dto.getPromptText());
        entity.setOutlineList(dto.getOutline());
        entity.setPptConfig(JSONObject.toJSONString(dto.getPptConfig()));

        saveOrUpdate(entity);

        return entity.getId();
    }

//    @Autowired
//    private IExamQuestionService examQuestionService ;
//
//    @Autowired
//    private IExamQuestionBankService examQuestionBankService;

//    @Override
//    public PPTGenerateSceneDto getPagerDetail(Long id) {
//
//        ExamPaperDTO dto = new ExamPaperDTO() ;
//
//        // 获取到试卷信息
//        PPTManagerEntity entity = getById(id) ;
//
//        dto.setId(id);
//        dto.setPagerName(entity.getTitle());
//        dto.setDifficulty(entity.getDifficulty());
//        dto.setPagerDesc(entity.getDescription());
//        dto.setPagerType("pager");
//
//        // 获取到试卷的题目信息
//        LambdaQueryWrapper<ExamQuestionEntity> waqWrapper = new LambdaQueryWrapper<>();
//        waqWrapper.eq(ExamQuestionEntity::getPagerId, id);
//
//        List<ExamQuestionEntity> questionList = examQuestionService.list(waqWrapper);
//
//        List<QuestionDTO> dtoList = questionList.stream().map(question -> {
//            QuestionDTO dtoInner = new QuestionDTO() ;
//
//            CopyOptions copyOptions = CopyOptions.create()
//                    .ignoreNullValue()
//                    .ignoreError()
//                    .ignoreCase();
//            BeanUtil.copyProperties(question , dtoInner , copyOptions);
//
//            if(StringUtils.isNotNull(question.getAnswers())){
//                dtoInner.setAnswers(JSONObject.parseArray(question.getAnswers() , JSONObject.class));
//            }else{
//                dtoInner.setAnswers(Collections.emptyList());
//            }
//
//            if(StringUtils.isNotNull(question.getBlanks())){
//                dtoInner.setBlanks(JSONObject.parseArray(question.getBlanks() , JSONObject.class));
//            }else{
//                dtoInner.setBlanks(Collections.emptyList());
//            }
//
//            dtoInner.setOrder(question.getSortOrder());
//            dtoInner.setId(String.valueOf(question.getId()));
//
//            return dtoInner;
//        }).toList();
//
//        dto.setQuestionList(dtoList);
//
//        return dto;
//    }
//
//    /**
//     * 保存试卷
//     * @param dto
//     */
//    private void savePagerQuestion(PPTGenerateSceneDto dto) {
//
//        String pagerName = dto.getPagerName() ;
//
//        PPTManagerEntity pagerEntity = new PPTManagerEntity();
//        BeanUtils.copyProperties(dto , pagerEntity);
//
//        pagerEntity.setTitle(pagerName);
//        pagerEntity.setDescription(dto.getPagerDesc());
//        pagerEntity.setDifficulty(dto.getDifficulty());
//
//        save(pagerEntity);
//
//        List<ExamQuestionEntity> questionList = new ArrayList<>() ;
//        List<QuestionDTO> list = dto.getQuestionList() ;
//
//        for(QuestionDTO questionDTO : list){
//            ExamQuestionEntity e = getExamQuestionEntity(dto, questionDTO);
//            e.setPagerId(pagerEntity.getId());
//            questionList.add(e);
//        }
//
//        examQuestionService.saveBatch(questionList);
//
//    }
//
//    @Override
//    public void savePager(ExamPaperDTO dto) {
//        String pagerType = dto.getPagerType() ;
//        if("pager".equals(pagerType)){  // 保存到试卷
//            savePagerQuestion(dto);
//        }else if("banks".equals(pagerType)){  // 保存到题库
//            savePagerBanks(dto);
//        }
//
//    }
//
//    /**
//     * 更新试卷信息
//     * @param dto
//     */
//    @Override
//    public void updatePager(PPTGenerateSceneDto dto) {
//        log.debug("dto = {}" , dto);
//
//        String pagerName = dto.getPagerName() ;
//
//        PPTManagerEntity pagerEntity = new PPTManagerEntity();
//        BeanUtils.copyProperties(dto , pagerEntity);
//
//        pagerEntity.setTitle(pagerName);
//        pagerEntity.setDescription(dto.getPagerDesc());
//        pagerEntity.setDifficulty(dto.getDifficulty());
//
//        update(pagerEntity);
//
//        List<ExamQuestionEntity> questionList = new ArrayList<>() ;
//        List<QuestionDTO> list = dto.getQuestionList() ;
//
//        ExamPaperDTO examPaperDTO = new ExamPaperDTO() ;
//        BeanUtil.copyProperties(dto , examPaperDTO);
//
//        for(QuestionDTO questionDTO : list){
//            ExamQuestionEntity e = getExamQuestionEntity(examPaperDTO, questionDTO);
//            e.setId(Long.valueOf(questionDTO.getId()));
//            e.setPagerId(pagerEntity.getId());
//            questionList.add(e);
//        }
//
//        examQuestionService.saveOrUpdateBatch(questionList);
//    }
//
//
//    @Override
//    public List<PPTManagerEntity> pagerListByPage(DatatablesPageBean page, PermissionQuery query) {
//
//        page.setPageNum(0);
//        page.setPageSize(20);
//
//        Page<PPTManagerEntity> pageBean = new Page<>(page.getPageNum(), page.getPageSize());
//
//        LambdaQueryWrapper<PPTManagerEntity> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(PPTManagerEntity::getOrgId, query.getOrgId()) ;
//        wrapper.orderByDesc(PPTManagerEntity::getAddTime) ;
//
//        pageBean = page(pageBean, wrapper);
//
//        return pageBean.getRecords();
//    }
//
//
//
//    @NotNull
//    private static ExamQuestionEntity getExamQuestionEntity(ExamPaperDTO dto, QuestionDTO questionDTO) {
//        ExamQuestionEntity e = new ExamQuestionEntity() ;
//
//        e.setOrgId(dto.getOrgId());
//        e.setOperatorId(dto.getOperatorId());
//        e.setDepartmentId(dto.getDepartmentId());
//
//        e.setAssessmentContent(questionDTO.getAssessmentContent());
//        e.setIsRequired(questionDTO.getIsRequired());
//        e.setScore(questionDTO.getScore());
//        e.setQuestion(questionDTO.getQuestion());
//        e.setAnswerAnalysis(questionDTO.getAnswerAnalysis());
//        e.setType(questionDTO.getType());
//        e.setSortOrder(questionDTO.getOrder());
//
//        if(StringUtils.isNotNull(questionDTO.getAnswers())){
//            e.setAnswers(JSONObject.toJSONString(questionDTO.getAnswers()));
//        }
//        if(StringUtils.isNotNull(questionDTO.getBlanks())){
//            e.setBlanks(JSONObject.toJSONString(questionDTO.getBlanks()));
//        }
//        e.setSortOrder(questionDTO.getOrder());
//        return e;
//    }
//
//    /**
//     * 保存题库
//     * @param dto
//     */
//    private void savePagerBanks(ExamPaperDTO dto) {
//
//        String pagerName = dto.getPagerName() ;
//
//        ExamQuestionBankEntity examQuestionBankEntity = new ExamQuestionBankEntity();
//        examQuestionBankEntity.setBankName(pagerName);
//
//        examQuestionBankService.save(examQuestionBankEntity);
//
//        List<ExamQuestionEntity> questionList = new ArrayList<>() ;
//
//        List<QuestionDTO> list = dto.getQuestionList() ;
//
//        for(QuestionDTO questionDTO : list){
//            ExamQuestionEntity e = getExamQuestionEntity(dto, questionDTO);
//            e.setPagerId(examQuestionBankEntity.getId());
//            questionList.add(e);
//        }
//
//        examQuestionService.saveBatch(questionList);
//    }


}
