package com.alineson.infra.smart.brain.service.impl;


import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alineson.infra.smart.brain.entity.SourceSystemEntity;
import com.alineson.infra.smart.brain.mapper.SourceSystemMapper;
import com.alineson.infra.smart.brain.service.ISourceSystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 来源系统Service业务层处理
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@Service
public class SourceSystemServiceImpl extends IBaseServiceImpl<SourceSystemEntity, SourceSystemMapper> implements ISourceSystemService {
    //日志记录
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(SourceSystemServiceImpl.class);
}
