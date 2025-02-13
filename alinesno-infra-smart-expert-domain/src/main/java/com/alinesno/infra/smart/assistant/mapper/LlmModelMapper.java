package com.alinesno.infra.smart.assistant.mapper;

import com.alinesno.infra.common.facade.mapper.repository.IBaseMapper;
import com.alinesno.infra.smart.assistant.entity.LlmModelEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 应用构建Mapper接口
 * 
 * @version 1.0.0
 * @author luoxiaodong
 */
@Mapper
public interface LlmModelMapper extends IBaseMapper<LlmModelEntity> {

}