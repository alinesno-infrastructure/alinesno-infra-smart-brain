package com.alinesno.infra.smart.assistant.scene.scene.pptCreation.mapper;

import com.alinesno.infra.common.facade.mapper.repository.IBaseMapper;
import com.alinesno.infra.smart.scene.entity.ExamPagerEntity;
import com.alinesno.infra.smart.scene.entity.ExamPagerSceneEntity;
import com.alinesno.infra.smart.scene.entity.PPTManagerEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PPTManagerMapper extends IBaseMapper<PPTManagerEntity> {
}
