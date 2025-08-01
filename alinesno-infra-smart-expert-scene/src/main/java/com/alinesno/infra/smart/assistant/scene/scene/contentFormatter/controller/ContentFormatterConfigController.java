package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.controller;

import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto.ContentFormatterRewriteConfigDto;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.IContentFormatterConfigService;
import com.alinesno.infra.smart.scene.entity.ContentFormatterConfigEntity;
import io.swagger.annotations.Api;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 智能文档配置表控制层
 */
@Slf4j
@Api(tags = "智能文档配置表")
@RestController
@RequestMapping("/api/infra/smart/assistant/scene/contentFormatterConfig/")
@Scope(SpringInstanceScope.PROTOTYPE)
public class ContentFormatterConfigController extends BaseController<ContentFormatterConfigEntity, IContentFormatterConfigService> {

    @Autowired
    private IContentFormatterConfigService service;

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

    /**
     * 初始化配置接口
     */
    @DataPermissionQuery
    @PostMapping("/initConfig")
    public AjaxResult initConfig(PermissionQuery query) {
        service.initConfig(query) ;
        return ok() ;
    }

    /**
     * 更新重写配置接口
     * @return
     */
    @DataPermissionSave
    @PostMapping("/updateRewriteConfig")
    public AjaxResult updateRewriteConfig(@RequestBody @Validated ContentFormatterRewriteConfigDto dto) {
        log.debug("dto = {}", dto);
        service.updateRewriteConfig(dto) ;
        return ok() ;
    }


    @Override
    public IContentFormatterConfigService getFeign() {
        return this.service;
    }

}