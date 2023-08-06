package com.alinesno.infra.base.notice.service.impl;

import com.alinesno.infra.base.notice.entity.NoticeTemplateEntity;
import com.alinesno.infra.base.notice.mapper.NoticeTemplateMapper;
import com.alinesno.infra.base.notice.service.INoticeTemplateService;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * NoticeTemplateEntity的Service实现类。
 * 继承自IBaseServiceImpl类并实现INoticeTemplateService接口。
 *
 * <在这里添加生成类的描述信息，一句话描述>
 *
 * @Service
 * @author luoxiaodong
 * @since 1.0.0
 */
@Service
public class NoticeTemplateServiceImpl extends IBaseServiceImpl<NoticeTemplateEntity, NoticeTemplateMapper> implements INoticeTemplateService {

	// 日志记录
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(NoticeTemplateServiceImpl.class);

}
