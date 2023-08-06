package com.alinesno.infra.base.notice.service.impl;

import com.alinesno.infra.base.notice.entity.MsgResponseEntity;
import com.alinesno.infra.base.notice.mapper.MsgResponseMapper;
import com.alinesno.infra.base.notice.service.IMsgResponseService;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * MsgResponseEntity的Service实现类。
 * 继承自IBaseServiceImpl类并实现IMsgResponseService接口。
 *
 * <在这里添加生成类的描述信息，一句话描述>
 *
 * @Service
 * @author luoxiaodong
 * @since 1.0.0
 */
@Service
public class MsgResponseServiceImpl extends IBaseServiceImpl<MsgResponseEntity, MsgResponseMapper> implements IMsgResponseService {

	// 日志记录
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(MsgResponseServiceImpl.class);

}
