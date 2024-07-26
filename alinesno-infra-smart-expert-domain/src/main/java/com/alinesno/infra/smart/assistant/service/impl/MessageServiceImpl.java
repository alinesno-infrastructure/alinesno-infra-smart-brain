package com.alinesno.infra.smart.assistant.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alinesno.infra.smart.assistant.entity.MessageNoticeEntity;
import com.alinesno.infra.smart.assistant.mapper.MessageNoticeMapper;
import com.alinesno.infra.smart.assistant.service.IMessageService;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@Service
public class MessageServiceImpl extends IBaseServiceImpl<MessageNoticeEntity, MessageNoticeMapper> implements IMessageService {

	// 日志记录
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(MessageServiceImpl.class);

}
