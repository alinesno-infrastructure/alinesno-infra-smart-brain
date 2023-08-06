package com.alineson.infra.smart.brain.service.impl;

import com.alineson.infra.smart.brain.entity.MsgResponseEntity;
import com.alineson.infra.smart.brain.mapper.MsgResponseMapper;
import com.alineson.infra.smart.brain.service.IMsgResponseService;
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
