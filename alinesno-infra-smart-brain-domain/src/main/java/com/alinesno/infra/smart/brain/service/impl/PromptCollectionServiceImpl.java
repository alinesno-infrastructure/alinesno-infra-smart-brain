package com.alinesno.infra.smart.brain.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.brain.entity.PromptCollectionEntity;
import com.alinesno.infra.smart.brain.mapper.PromptCollectionMapper;
import com.alinesno.infra.smart.brain.service.IPromptCollectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 资产收藏Service业务层处理
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@Service
public class PromptCollectionServiceImpl extends IBaseServiceImpl<PromptCollectionEntity, PromptCollectionMapper> implements IPromptCollectionService {
    //日志记录
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(PromptCollectionServiceImpl.class);
}
