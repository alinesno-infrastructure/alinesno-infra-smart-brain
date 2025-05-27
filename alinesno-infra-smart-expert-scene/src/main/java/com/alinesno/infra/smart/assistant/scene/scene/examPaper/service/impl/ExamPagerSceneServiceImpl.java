package com.alinesno.infra.smart.assistant.scene.scene.examPaper.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.mapper.ExamPagerSceneMapper;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.service.IExamPagerSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.service.IExamQuestionService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.scene.dto.RoleListRequestDto;
import com.alinesno.infra.smart.scene.dto.UpdateSceneAgentDto;
import com.alinesno.infra.smart.scene.entity.ExamPagerSceneEntity;
import com.alinesno.infra.smart.utils.RoleUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
