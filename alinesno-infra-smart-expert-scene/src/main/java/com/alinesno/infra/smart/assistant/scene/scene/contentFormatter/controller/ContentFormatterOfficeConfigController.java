package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.controller;

import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.IContentFormatterOfficeConfigService;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.IContentFormatterParseService;
import com.alinesno.infra.smart.scene.entity.ContentFormatterOfficeConfigEntity;
import com.alinesno.infra.smart.scene.entity.ContentFormatterParseEntity;
import io.swagger.annotations.Api;
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
 * 审核配置管理中心
 */
@Slf4j
@RestController
@RequestMapping("/api/infra/smart/assistant/scene/contentFormatterOfficeConfig/")
@Scope(SpringInstanceScope.PROTOTYPE)
public class ContentFormatterOfficeConfigController extends BaseController<ContentFormatterOfficeConfigEntity, IContentFormatterOfficeConfigService> {

    @Autowired
    private IContentFormatterOfficeConfigService service;

    /**
     * 获取BusinessLogEntity的DataTables数据。
     *
     * @param request HttpServletRequest对象。
     * @param model Model对象。
     * @param page DatatablesPageBean对象。
     * @return 包含DataTables数据的TableDataInfo对象。
     */
    @DataPermissionScope
    @ResponseBody
    @PostMapping("/datatables")
    public TableDataInfo datatables(HttpServletRequest request, Model model, DatatablesPageBean page) {
        log.debug("page = {}", ToStringBuilder.reflectionToString(page));

        return this.toPage(model, this.getFeign(), page);
    }

    @Override
    public IContentFormatterOfficeConfigService getFeign() {
        return this.service;
    }

}