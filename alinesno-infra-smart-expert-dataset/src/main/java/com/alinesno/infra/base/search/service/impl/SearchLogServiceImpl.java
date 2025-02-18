package com.alinesno.infra.base.search.service.impl;

import com.alinesno.infra.base.search.entity.SearchLogEntity;
import com.alinesno.infra.base.search.mapper.SearchLogMapper;
import com.alinesno.infra.base.search.service.ISearchLogService;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 搜索日志Service业务层处理
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@Service
public class SearchLogServiceImpl extends IBaseServiceImpl<SearchLogEntity, SearchLogMapper> implements ISearchLogService {
}