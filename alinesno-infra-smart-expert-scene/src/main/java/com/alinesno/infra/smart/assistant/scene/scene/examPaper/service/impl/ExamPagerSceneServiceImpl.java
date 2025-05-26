package com.alinesno.infra.smart.assistant.scene.scene.examPaper.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ExamPaperDTO;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.QuestionDTO;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.mapper.ExamPagerSceneMapper;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.service.IExamPagerSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.service.IExamPagerService;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.service.IExamQuestionBankService;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.service.IExamQuestionService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.scene.dto.RoleListRequestDto;
import com.alinesno.infra.smart.scene.dto.UpdateSceneAgentDto;
import com.alinesno.infra.smart.scene.entity.ExamPagerEntity;
import com.alinesno.infra.smart.scene.entity.ExamPagerSceneEntity;
import com.alinesno.infra.smart.scene.entity.ExamQuestionBankEntity;
import com.alinesno.infra.smart.scene.entity.ExamQuestionEntity;
import com.alinesno.infra.smart.utils.RoleUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
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
public class ExamPagerSceneServiceImpl extends IBaseServiceImpl<ExamPagerSceneEntity , ExamPagerSceneMapper> implements IExamPagerSceneService {

    @Autowired
    private IIndustryRoleService roleService ;

    @Autowired
    private IExamPagerService examPagerService;

    @Autowired
    private IExamQuestionBankService examQuestionBankService;

    @Autowired
    private IExamQuestionService examQuestionService;

    @Override
    public void updateSceneAgents(UpdateSceneAgentDto dto) {
        long sceneId = dto.getSceneId() ;

        LambdaQueryWrapper<ExamPagerSceneEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamPagerSceneEntity::getSceneId, sceneId) ;
        long count = count(wrapper) ;

        ExamPagerSceneEntity entity = new ExamPagerSceneEntity() ;
        if(count > 0){
            entity = getOne(wrapper) ;
        }

        long questionGeneratorEngineer = RoleUtils.findSelectAgentIdByCode(dto , "questionGenerator") ;
        long answerCheckerEngineer = RoleUtils.findSelectAgentIdByCode(dto , "answerChecker") ;
        long paperGeneratorEngineer = RoleUtils.findSelectAgentIdByCode(dto , "paperGenerator") ;

        entity.setQuestionGeneratorEngineer(questionGeneratorEngineer) ;
        entity.setAnswerCheckerEngineer(answerCheckerEngineer) ;
        entity.setPaperGeneratorEngineer(paperGeneratorEngineer) ;

        entity.setSceneId(sceneId);

        saveOrUpdate(entity) ;
    }

    @Override
    public List<IndustryRoleEntity> getRoleList(RoleListRequestDto dto) {
        PermissionQuery query = new PermissionQuery() ;
        BeanUtils.copyProperties(dto , query) ;

        ExamPagerSceneEntity entity = getBySceneId(dto.getSceneId(), query);
        if(entity == null){
            return Collections.emptyList() ;
        }

        String agentTypeCode = dto.getAgentTypeCode() ;

        if("questionGenerator".equals(agentTypeCode)){
            return roleService.listByIds(List.of(entity.getQuestionGeneratorEngineer()));
        }else if("answerChecker".equals(agentTypeCode)){
            return roleService.listByIds(List.of(entity.getAnswerCheckerEngineer()));
        }else if("paperGenerator".equals(agentTypeCode)){
            return roleService.listByIds(List.of(entity.getPaperGeneratorEngineer()));
        }

        return Collections.emptyList() ;
    }


    @Override
    public ExamPagerSceneEntity getBySceneId(long sceneId, PermissionQuery query) {

        LambdaQueryWrapper<ExamPagerSceneEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamPagerSceneEntity::getSceneId, sceneId) ;

        return getOne(wrapper) ;
    }

    @Override
    public void savePager(ExamPaperDTO dto) {

        String pagerType = dto.getPagerType() ;

        if("pager".equals(pagerType)){  // 保存到试卷
            savePagerQuestion(dto);
        }else if("banks".equals(pagerType)){  // 保存到题库
            savePagerBanks(dto);
        }

    }

    @Override
    public List<ExamPagerEntity> pagerListByPage(DatatablesPageBean page, PermissionQuery query) {

        page.setPageNum(0);
        page.setPageSize(20);

        Page<ExamPagerEntity> pageBean = new Page<>(page.getPageNum(), page.getPageSize());

        LambdaQueryWrapper<ExamPagerEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamPagerEntity::getOrgId, query.getOrgId()) ;

        pageBean = examPagerService.page(pageBean, wrapper);

        return pageBean.getRecords();
    }

    /**
     * 保存题库
     * @param dto
     */
    private void savePagerBanks(ExamPaperDTO dto) {

        String pagerName = dto.getPagerName() ;

        ExamQuestionBankEntity examQuestionBankEntity = new ExamQuestionBankEntity();
        examQuestionBankEntity.setBankName(pagerName);

        examQuestionBankService.save(examQuestionBankEntity);

        List<ExamQuestionEntity> questionList = new ArrayList<>() ;

        List<QuestionDTO> list = dto.getQuestionList() ;
        for(QuestionDTO questionDTO : list){
            ExamQuestionEntity e = new ExamQuestionEntity() ;
            BeanUtils.copyProperties(questionDTO , e);
            e.setId(null);
            e.setBankId(examQuestionBankEntity.getId());
            e.setSortOrder(questionDTO.getOrder());

            questionList.add(e);
        }

        examQuestionService.saveBatch(questionList);
    }

    /**
     * 保存试卷
     * @param dto
     */
    private void savePagerQuestion(ExamPaperDTO dto) {

        String pagerName = dto.getPagerName() ;

        ExamPagerEntity pagerEntity = new ExamPagerEntity();
        BeanUtils.copyProperties(dto , pagerEntity);
        pagerEntity.setTitle(pagerName);

        examPagerService.save(pagerEntity);

        List<ExamQuestionEntity> questionList = new ArrayList<>() ;

        List<QuestionDTO> list = dto.getQuestionList() ;

        for(QuestionDTO questionDTO : list){

            ExamQuestionEntity e = new ExamQuestionEntity() ;

            BeanUtils.copyProperties(dto , e);
            BeanUtils.copyProperties(questionDTO , e);

            e.setId(null);
            e.setPagerId(pagerEntity.getId());

            e.setAnswers(JSONObject.toJSONString(questionDTO.getAnswers()));
            e.setBlanks(JSONObject.toJSONString(questionDTO.getBlanks()));
            e.setSortOrder(questionDTO.getOrder());

            questionList.add(e);
        }

        examQuestionService.saveBatch(questionList);

    }
}
