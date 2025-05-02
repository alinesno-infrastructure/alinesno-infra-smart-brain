package com.alinesno.infra.base.search.service.impl;

import com.alinesno.infra.base.search.entity.DatasetParseLogEntity;
import com.alinesno.infra.base.search.mapper.DatasetParseLogMapper;
import com.alinesno.infra.base.search.service.IDatasetParseLogService;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DatasetParseLogServiceImpl extends IBaseServiceImpl<DatasetParseLogEntity, DatasetParseLogMapper> implements IDatasetParseLogService {
}    