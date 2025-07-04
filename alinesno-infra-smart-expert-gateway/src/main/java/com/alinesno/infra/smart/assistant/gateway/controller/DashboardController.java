package com.alinesno.infra.smart.assistant.gateway.controller;


import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.smart.im.dto.HomePageDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *
 */
@Slf4j
@Api(tags = "IndustryRole")
@RestController
@Scope("prototype")
@RequestMapping("/api/infra/smart/assistant/dashboard")
public class DashboardController {

    @Autowired
    private IIndustryRoleService roleService;

    // 获取到最新更新的8个角色信息
    @DataPermissionQuery
    @RequestMapping("/getNewestRole")
    public AjaxResult getNewestRole(PermissionQuery query) {

        List<IndustryRoleEntity> list = roleService.getNewestRole(query);

        return AjaxResult.success(list) ;
    }

}
