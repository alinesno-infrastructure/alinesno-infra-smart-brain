package com.alinesno.infra.smart.assistant.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.entity.ThirdPartyAppEntity;
import com.alinesno.infra.smart.assistant.mapper.ThirdPartyAppMapper;
import com.alinesno.infra.smart.assistant.service.IThirdPartyAppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@Service
public class ThirdPartyAppServiceImpl extends IBaseServiceImpl<ThirdPartyAppEntity, ThirdPartyAppMapper> implements IThirdPartyAppService {

	// 日志记录
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(ThirdPartyAppServiceImpl.class);

}
