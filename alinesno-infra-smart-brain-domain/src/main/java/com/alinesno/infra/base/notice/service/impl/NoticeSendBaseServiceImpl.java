package com.alinesno.infra.base.notice.service.impl;

import com.alinesno.infra.base.notice.entity.NoticeSendBaseEntity;
import com.alinesno.infra.base.notice.mapper.NoticeSendBaseMapper;
import com.alinesno.infra.base.notice.service.INoticeSendBaseService;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * NoticeSendBaseEntity的Service实现类。
 * 继承自IBaseServiceImpl类并实现INoticeSendBaseService接口。
 *
 * @author luoxiaodong
 * @since 1.0.0
 */
@Service
public class NoticeSendBaseServiceImpl extends IBaseServiceImpl<NoticeSendBaseEntity, NoticeSendBaseMapper> implements INoticeSendBaseService {

	// 日志记录
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(NoticeSendBaseServiceImpl.class);

}
