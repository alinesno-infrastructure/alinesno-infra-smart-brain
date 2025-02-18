package com.alinesno.infra.base.search.mapper;

import com.alinesno.infra.base.search.entity.SearchLogEntity;
import com.alinesno.infra.common.facade.mapper.repository.IBaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 搜索日志Mapper接口
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@Mapper
public interface SearchLogMapper extends IBaseMapper<SearchLogEntity> {
}