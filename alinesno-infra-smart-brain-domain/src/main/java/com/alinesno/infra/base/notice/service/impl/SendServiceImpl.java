package com.alinesno.infra.base.notice.service.impl;

import com.alinesno.infra.base.notice.entity.SendEntity;
import com.alinesno.infra.base.notice.mapper.SendMapper;
import com.alinesno.infra.base.notice.service.ISendService;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * SendEntity的Service实现类。
 * 继承自IBaseServiceImpl类并实现ISendService接口。
 *
 * @author luoxiaodong
 * @since 1.0.0
 */
@Service
public class SendServiceImpl extends IBaseServiceImpl<SendEntity, SendMapper> implements ISendService {

	// 日志记录
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(SendServiceImpl.class);

}
