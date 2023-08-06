package com.alinesno.infra.base.notice.service.impl;

import com.alinesno.infra.base.notice.entity.NoticeSummaryEntity;
import com.alinesno.infra.base.notice.mapper.NoticeSummaryMapper;
import com.alinesno.infra.base.notice.service.INoticeSummaryService;
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
