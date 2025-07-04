package com.alinesno.infra.smart.assistant.scene.common.examPaper.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.scene.common.examPaper.dto.ExamSubmissionDto;
import com.alinesno.infra.smart.assistant.scene.common.examPaper.mapper.ExamineeMapper;
import com.alinesno.infra.smart.assistant.scene.common.examPaper.service.IExamineeService;
import com.alinesno.infra.smart.scene.entity.ExamineeEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 考生ServiceImpl类
 */
@Slf4j
@Service
public class ExamineeServiceImpl extends IBaseServiceImpl<ExamineeEntity, ExamineeMapper> implements IExamineeService {

    /**
     * 初始化考生信息
     * 如果不存在，则添加，存在（通过examineeId来判断考生是否存在），则更新并返回
     * @param submission
     * @return
     */
    @Override
    public String initExaminee(ExamSubmissionDto submission) {
        log.debug("submission = {}", submission);

        LambdaQueryWrapper<ExamineeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExamineeEntity::getExamineeId, submission.getExamineeId());

        ExamineeEntity examinee = getOne(queryWrapper);
        if (examinee == null) {

            examinee = new ExamineeEntity() ;
            examinee.setExamineeId(submission.getExamineeId());
            examinee.setName(submission.getName());

            this.save(examinee);
        }

        return String.valueOf(examinee.getId());
    }
}
