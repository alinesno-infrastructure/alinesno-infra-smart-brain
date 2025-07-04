package com.alinesno.infra.smart.assistant.scene.common.examPaper.mapper;

import com.alinesno.infra.common.facade.mapper.repository.IBaseMapper;
import com.alinesno.infra.smart.scene.entity.ExamPagerEntity;
import com.alinesno.infra.smart.scene.entity.ExamPagerSceneEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExamPagerMapper extends IBaseMapper<ExamPagerEntity> {
}
