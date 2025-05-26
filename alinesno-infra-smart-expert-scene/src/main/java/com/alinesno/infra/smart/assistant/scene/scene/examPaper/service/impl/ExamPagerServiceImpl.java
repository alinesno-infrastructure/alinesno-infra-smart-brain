package com.alinesno.infra.smart.assistant.scene.scene.examPaper.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ExamPaperDTO;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.QuestionDTO;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.mapper.ExamPagerMapper;
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
public class ExamPagerServiceImpl extends IBaseServiceImpl<ExamPagerEntity , ExamPagerMapper> implements IExamPagerService {

}
