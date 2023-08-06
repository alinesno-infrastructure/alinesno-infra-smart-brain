package com.alinesno.infra.base.notice.service.impl;

import com.alinesno.infra.base.notice.entity.ProjectEntity;
import com.alinesno.infra.base.notice.mapper.ProjectMapper;
import com.alinesno.infra.base.notice.service.IProjectService;
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
public class ProjectServiceImpl extends IBaseServiceImpl<ProjectEntity, ProjectMapper> implements IProjectService {

	// 日志记录
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(ProjectServiceImpl.class);

}