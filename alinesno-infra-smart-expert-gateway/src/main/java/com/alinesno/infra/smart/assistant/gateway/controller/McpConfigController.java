package com.alinesno.infra.smart.assistant.gateway.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.login.account.CurrentAccountJwt;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.api.McpConfigDto;
import com.alinesno.infra.smart.assistant.entity.McpConfigEntity;
import com.alinesno.infra.smart.assistant.service.IMcpConfigService;
import io.jsonwebtoken.lang.Assert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * MCP配置控制器
 */
@Api(tags = "MCP配置管理")
@RestController
@RequestMapping("/api/infra/smart/assistant/mcpConfig")
public class McpConfigController extends BaseController<McpConfigEntity, IMcpConfigService> {

    @Autowired
    private IMcpConfigService service;

    @Override
    public IMcpConfigService getFeign() {
        return service;
    }

    @DataPermissionQuery
    @ApiOperation("获取当前组织的MCP配置")
    @GetMapping("/getByOrgId")
    public AjaxResult getByOrgId() {
        McpConfigEntity config = service.getByOrgId(CurrentAccountJwt.get().getOrgId());
        return AjaxResult.success("操作成功" , config) ;
    }

    @DataPermissionSave
    @ApiOperation("保存或更新MCP配置")
    @PostMapping("/saveOrUpdate")
    public AjaxResult saveOrUpdate(@RequestBody McpConfigDto dto) {

        McpConfigEntity entity = new McpConfigEntity();
        BeanUtil.copyProperties(dto , entity);

        service.saveOrUpdate(entity);
        return AjaxResult.success("保存成功" , entity);
    }

    @ApiOperation("验证MCP地址是否有效")
    @PostMapping("/validateUrl")
    public AjaxResult validateUrl(@RequestParam String mcpUrl) {
        boolean isValid = service.validateMcpUrl(mcpUrl);
        Assert.isTrue(isValid , "地址有效");
        return ok();
    }
}