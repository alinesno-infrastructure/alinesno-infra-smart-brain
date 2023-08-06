package com.alinesno.infra.base.notice.service.impl;

import com.alinesno.infra.base.notice.entity.RequestRecordEntity;
import com.alinesno.infra.base.notice.mapper.RequestRecordMapper;
import com.alinesno.infra.base.notice.service.IRequestRecordService;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author WeiXiaoJin
 * @version 1.0.0
 */
@Service
public class RequestRecordServiceImpl extends IBaseServiceImpl<RequestRecordEntity, RequestRecordMapper> implements IRequestRecordService {

	// 日志记录
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(RequestRecordServiceImpl.class);

}
