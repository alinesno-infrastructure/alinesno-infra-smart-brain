package com.alinesno.infra.base.notice.service.impl;

import com.alinesno.infra.base.notice.entity.StrategyEntity;
import com.alinesno.infra.base.notice.mapper.StrategyMapper;
import com.alinesno.infra.base.notice.service.IStrategyService;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * StrategyEntity的Service实现类。
 * 继承自IBaseServiceImpl类并实现IStrategyService接口。
 *
 * @author luoxiaodong
 * @since 1.0.0
 */
@Service
public class StrategyServiceImpl extends IBaseServiceImpl<StrategyEntity, StrategyMapper> implements IStrategyService {

	// 日志记录
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(StrategyServiceImpl.class);

}
