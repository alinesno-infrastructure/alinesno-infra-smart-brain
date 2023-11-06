package com.alinesno.infra.smart.brain.service.impl;


import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.brain.entity.KnowledgeEntity;
import com.alinesno.infra.smart.brain.service.IKnowledgeService;
import com.alinesno.infra.smart.brain.mapper.KnowledgeMapper;
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
public class KnowledgeServiceImpl extends IBaseServiceImpl<KnowledgeEntity, KnowledgeMapper> implements IKnowledgeService {
    //日志记录
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(KnowledgeServiceImpl.class);
}
