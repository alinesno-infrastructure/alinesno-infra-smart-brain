package com.alinesno.infra.base.notice.api.controller;

import com.alinesno.infra.base.notice.entity.NoticeSummaryEntity;
import com.alinesno.infra.base.notice.service.INoticeSummaryService;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.core.rest.BaseController;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import io.swagger.annotations.Api;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
/**
 * 处理与NoticeSummaryEntity相关的请求的Controller。
 * 继承自BaseController类并实现INoticeSummaryService接口。
 *
 * @author luoxiaodong
 * @since 1.0.0
 */
@Api(tags = "Notice Summary")
@RestController
@Scope(SpringInstanceScope.PROTOTYPE)
@RequestMapping("/api/infra/base/noticeSummary/NoticeSummary")
public class NoticeSummaryController extends BaseController<NoticeSummaryEntity, INoticeSummaryService> {

	// 日志记录
	private static final Logger log = LoggerFactory.getLogger(NoticeSummaryController.class);

	@Autowired
	private INoticeSummaryService service;

	/**
	 * 处理 "/api/infra/base/noticeSummary/NoticeSummary/datatables" 的POST请求。
	 * 获取NoticeSummaryEntity的DataTables数据。
	 *
	 * @param request HttpServletRequest对象。
	 * @param model Model对象。
	 * @param page DatatablesPageBean对象。
	 * @return 包含DataTables数据的TableDataInfo对象。
	 */
	@ResponseBody
	@PostMapping("/datatables")
	public TableDataInfo datatables(HttpServletRequest request, Model model, DatatablesPageBean page) {
		log.debug("page = {}", ToStringBuilder.reflectionToString(page));
		return this.toDataInfo(model, this.getFeign(), page);
	}

	@Override
	public INoticeSummaryService getFeign() {
		return this.service;
	}
}
