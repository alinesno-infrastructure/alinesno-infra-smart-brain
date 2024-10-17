package com.alinesno.infra.smart.assistant.gateway.session;

import com.alinesno.infra.common.web.log.utils.SpringUtils;
import com.alinesno.infra.smart.assistant.entity.ProjectAccountEntity;
import com.alinesno.infra.smart.assistant.entity.ProjectEntity;
import com.alinesno.infra.smart.assistant.service.IProjectAccountService;
import com.alinesno.infra.smart.assistant.service.IProjectService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;

/**
 * 获取当前应用
 *
 * @author luoxiaodong
 * @since 1.0.0
 */
@Slf4j
//@Component
public class CurrentProjectSession {

//	@Autowired
//	private IProjectAccountService projectAccountService;  ;
//
//	@Autowired
//	private IProjectService managerProjectService ;

	public static ProjectEntity get() {
		// TODO 待处理账号获取异常的问题

		IProjectService managerProjectService = SpringUtils.getBean(IProjectService.class);

		//  CurrentAccountJwt.getUserId();
		long userId = 1L ;

		return managerProjectService.getProjectByAccountId(userId) ;
	}

	public static void set(long applicationId) {

		IProjectAccountService projectAccountService = SpringUtils.getBean(IProjectAccountService.class);

		//  CurrentAccountJwt.getUserId();
		long userId = 1L ;

		// 查询当前用户配置记录
		long count = projectAccountService.count(new LambdaQueryWrapper<ProjectAccountEntity>()
				.eq(ProjectAccountEntity::getAccountId , userId)) ;

		ProjectAccountEntity e = new ProjectAccountEntity() ;

		e.setAccountId(userId);
		e.setApplicationId(applicationId);
		e.setAppOrder(count+1);

		projectAccountService.save(e);
	}

}
