package com.alinesno.infra.base.search.gateway.controller;

import com.alinesno.infra.base.search.api.SearchConfigDto;
import com.alinesno.infra.base.search.service.ISearchConfigService;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.login.account.CurrentAccountJwt;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@Api(tags = "Project")
@RestController
@Scope("prototype")
@RequestMapping("/api/infra/base/search/configuration")
public class SearchConfigController {

    @Autowired
    private ISearchConfigService searchConfigService;

    @DataPermissionSave
    @PostMapping("/update")
    public AjaxResult saveSearchConfig(@Valid @RequestBody SearchConfigDto searchConfigDTO) {
        log.debug("接收到的配置信息: {}" , searchConfigDTO);
        searchConfigService.saveConfiguration(searchConfigDTO);
        return AjaxResult.success("配置保存成功");
    }

    /**
     * 查询配置
     */
    @GetMapping("/find")
    public AjaxResult findSearchConfig() {
        log.debug("查询配置信息");
        return AjaxResult.success(searchConfigService.findConfiguration(CurrentAccountJwt.get().getOrgId()));
    }
}