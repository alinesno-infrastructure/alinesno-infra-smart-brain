package com.alinesno.infra.base.search.mapper;

import com.alinesno.infra.base.search.entity.DatasetParseLogEntity;
import com.alinesno.infra.common.facade.mapper.repository.IBaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DatasetParseLogMapper extends IBaseMapper<DatasetParseLogEntity> {
}    