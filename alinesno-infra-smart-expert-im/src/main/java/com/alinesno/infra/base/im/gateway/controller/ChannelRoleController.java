package com.alinesno.infra.base.im.gateway.controller;

import com.alinesno.infra.base.im.entity.ChannelRoleEntity;
import com.alinesno.infra.base.im.service.IChannelRoleService;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 处理与BusinessLogEntity相关的请求的Controller。
 * 继承自BaseController类并实现IBusinessLogService接口。
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@Slf4j
@RestController
@Scope(SpringInstanceScope.PROTOTYPE)
@RequestMapping("/api/infra/base/im/channelUser")
public class ChannelRoleController extends BaseController<ChannelRoleEntity, IChannelRoleService> {

    @Autowired
    private IChannelRoleService service;

    @Autowired
    private IIndustryRoleService roleService ;

    /**
     * 获取BusinessLogEntity的DataTables数据。
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

        // return smartAssistantConsumer.getAgentList(page.getPageNum() , page.getPageSize() , "") ;

        Page<IndustryRoleEntity> svcPage = Page.of(page.getPageNum() , page.getPageSize()) ;
        svcPage = roleService.page(svcPage) ;

        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setRows(svcPage.getRecords());
        tableDataInfo.setTotal(svcPage.getTotal());

        return tableDataInfo;
    }

    @Override
    public IChannelRoleService getFeign() {
        return this.service;
    }
}

