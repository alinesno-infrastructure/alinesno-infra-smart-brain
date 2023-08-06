package com.alineson.infra.smart.brain.service.impl;

import com.alineson.infra.smart.brain.entity.SendSettingsEntity;
import com.alineson.infra.smart.brain.mapper.SendSettingsMapper;
import com.alineson.infra.smart.brain.service.ISendSettingsService;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * SendSettingsEntity的Service实现类。
 * 继承自IBaseServiceImpl类并实现ISendSettingsService接口。
 *
 * @author luoxiaodong
 * @since 1.0.0
 */
@Service
public class SendSettingsServiceImpl extends IBaseServiceImpl<SendSettingsEntity, SendSettingsMapper> implements ISendSettingsService {

	// 日志记录
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(SendSettingsServiceImpl.class);

}
