package com.alinesno.infra.smart.assistant.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.entity.ApiKeyEntity;
import com.alinesno.infra.smart.assistant.mapper.ApiKeyMapper;
import com.alinesno.infra.smart.assistant.service.IApiKeyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 应用构建Service业务层处理
 * 
 * @version 1.0.0
 * @author luoxiaodong
 */
@Slf4j
@Service
public class ApiKeyServiceImpl extends IBaseServiceImpl<ApiKeyEntity, ApiKeyMapper> implements IApiKeyService {

}