package com.alinesno.infra.smart.assistant.screen.mapper;

import com.alinesno.infra.common.facade.mapper.repository.IBaseMapper;
import com.alinesno.infra.smart.assistant.screen.entity.ScreenEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ScreenMapper extends IBaseMapper<ScreenEntity> {
}
