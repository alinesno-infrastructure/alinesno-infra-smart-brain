package com.alinesno.infra.base.notice.service.impl;

import com.alinesno.infra.base.notice.entity.SendHistoryEntity;
import com.alinesno.infra.base.notice.mapper.SendHistoryMapper;
import com.alinesno.infra.base.notice.service.ISendHistoryService;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * SendHistoryEntity的Service实现类。
 * 继承自IBaseServiceImpl类并实现ISendHistoryService接口。
 *
 * @author luoxiaodong
 * @since 1.0.0
 */
@Service
public class SendHistoryServiceImpl extends IBaseServiceImpl<SendHistoryEntity, SendHistoryMapper> implements ISendHistoryService {

	// 日志记录
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(SendHistoryServiceImpl.class);

}
