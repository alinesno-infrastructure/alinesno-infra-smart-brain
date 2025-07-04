package com.alinesno.infra.smart.assistant.scene.common.examPaper.mapper;

import com.alinesno.infra.common.facade.mapper.repository.IBaseMapper;
import com.alinesno.infra.smart.scene.entity.ExamPagerSceneEntity;
import com.alinesno.infra.smart.scene.entity.ExamQuestionEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExamQuestionMapper extends IBaseMapper<ExamQuestionEntity> {
}
