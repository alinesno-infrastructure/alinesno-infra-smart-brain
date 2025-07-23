package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.controller;

import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.scene.common.utils.HealthCheckUtil;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto.ContentFormatterOfficeConfigDto;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.IContentFormatterOfficeConfigService;
import com.alinesno.infra.smart.scene.entity.ContentFormatterOfficeConfigEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 审核配置管理中心
 */
@Slf4j
@Api(tags = "Office解析服务配置")
@RestController
@RequestMapping("/api/infra/smart/assistant/scene/contentFormatterOfficeConfig/")
@Scope(SpringInstanceScope.PROTOTYPE)
@Validated
public class ContentFormatterOfficeConfigController extends BaseController<ContentFormatterOfficeConfigEntity, IContentFormatterOfficeConfigService> {

    @Autowired
    private IContentFormatterOfficeConfigService service;

    @DataPermissionQuery
    @GetMapping("/getOfficeConfig")
    public AjaxResult getOfficeConfig(PermissionQuery query) {
        ContentFormatterOfficeConfigEntity config = service.getConfig(query.getOrgId());
        if (config == null) {
            return AjaxResult.success();
        }
        return AjaxResult.success(ContentFormatterOfficeConfigDto.fromEntity(config));
    }

    @DataPermissionSave
    @PostMapping("/addOfficeConfig")
    public AjaxResult addOfficeConfig(@Valid @RequestBody ContentFormatterOfficeConfigDto dto) {
        ContentFormatterOfficeConfigEntity entity = dto.toEntity();

        // 简单检查
        if (!HealthCheckUtil.isServiceOk(dto.getToolPath())) {
            return AjaxResult.error("请检查服务是否正常启动，查询链接异常.");
        }

        service.save(entity);
        return AjaxResult.success("配置添加成功" , entity.getId());
    }

    @DataPermissionSave
    @PutMapping("/updateOfficeConfig")
    public AjaxResult updateOfficeConfig(@Valid @RequestBody ContentFormatterOfficeConfigDto dto) {
        if (dto.getId() == null) {
            return AjaxResult.error("ID不能为空");
        }

        // 简单检查
        if (!HealthCheckUtil.isServiceOk(dto.getToolPath())) {
            return AjaxResult.error("请检查服务是否正常启动，解析地址连接异常.");
        }

        ContentFormatterOfficeConfigEntity entity = dto.toEntity();
        service.update(entity);
        return AjaxResult.success("配置更新成功");
    }

    @Override
    public IContentFormatterOfficeConfigService getFeign() {
        return this.service;
    }
}