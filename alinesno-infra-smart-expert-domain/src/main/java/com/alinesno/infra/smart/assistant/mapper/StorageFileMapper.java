package com.alinesno.infra.smart.assistant.mapper;

import com.alinesno.infra.common.facade.mapper.repository.IBaseMapper;
import com.alinesno.infra.smart.assistant.entity.StorageFileEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StorageFileMapper extends IBaseMapper<StorageFileEntity> {
}
