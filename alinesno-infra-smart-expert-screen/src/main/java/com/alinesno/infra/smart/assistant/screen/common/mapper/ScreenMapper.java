package com.alinesno.infra.smart.assistant.screen.common.mapper;

import com.alinesno.infra.common.facade.mapper.repository.IBaseMapper;
import com.alinesno.infra.smart.assistant.screen.core.entity.ScreenEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ScreenMapper extends IBaseMapper<ScreenEntity> {
}
