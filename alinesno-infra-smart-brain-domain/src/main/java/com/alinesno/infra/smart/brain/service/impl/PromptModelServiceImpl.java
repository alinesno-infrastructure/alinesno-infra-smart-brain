package com.alinesno.infra.smart.brain.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.brain.entity.PromptModelEntity;
import com.alinesno.infra.smart.brain.service.IPromptModelService;
import com.alinesno.infra.smart.brain.mapper.PromptModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


/**
 *
 * 模型管理Service业务层处理
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@Service
public class PromptModelServiceImpl extends IBaseServiceImpl<PromptModelEntity, PromptModelMapper> implements IPromptModelService {
    //日志记录
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(PromptModelServiceImpl.class);
}
