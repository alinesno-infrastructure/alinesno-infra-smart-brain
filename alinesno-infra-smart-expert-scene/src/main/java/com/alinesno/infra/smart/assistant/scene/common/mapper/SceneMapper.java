package com.alinesno.infra.smart.assistant.scene.common.mapper;

import com.alinesno.infra.common.facade.mapper.repository.IBaseMapper;
import com.alinesno.infra.smart.assistant.scene.core.entity.SceneEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SceneMapper extends IBaseMapper<SceneEntity> {
}
