package com.alineson.infra.smart.brain.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alineson.infra.smart.brain.entity.PromptCatalogEntity;
import com.alineson.infra.smart.brain.mapper.PromptCatalogMapper;
import com.alineson.infra.smart.brain.service.IPromptCatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Prompt指令类型Service业务层处理
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@Service
public class PromptCatalogServiceImpl extends IBaseServiceImpl<PromptCatalogEntity, PromptCatalogMapper> implements IPromptCatalogService {
    //日志记录
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(PromptCatalogServiceImpl.class);
}
