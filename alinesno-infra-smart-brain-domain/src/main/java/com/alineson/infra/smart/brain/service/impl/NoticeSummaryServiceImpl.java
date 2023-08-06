package com.alineson.infra.smart.brain.service.impl;

import com.alineson.infra.smart.brain.entity.NoticeSummaryEntity;
import com.alineson.infra.smart.brain.mapper.NoticeSummaryMapper;
import com.alineson.infra.smart.brain.service.INoticeSummaryService;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * NoticeSummaryEntity的Service实现类。
 * 继承自IBaseServiceImpl类并实现INoticeSummaryService接口。
 *
 * @author luoxiaodong
 * @since 1.0.0
 */
@Service
public class NoticeSummaryServiceImpl extends IBaseServiceImpl<NoticeSummaryEntity, NoticeSummaryMapper> implements INoticeSummaryService {

	// 日志记录
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(NoticeSummaryServiceImpl.class);

}
