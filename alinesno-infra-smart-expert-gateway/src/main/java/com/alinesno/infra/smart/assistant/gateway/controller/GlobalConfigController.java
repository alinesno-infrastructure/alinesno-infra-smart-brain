package com.alinesno.infra.smart.assistant.gateway.controller;

import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.login.account.CurrentAccountJwt;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.api.GlobalConfigDTO;
import com.alinesno.infra.smart.assistant.entity.GlobalConfigEntity;
import com.alinesno.infra.smart.assistant.service.IGlobalConfigService;
import io.swagger.annotations.Api;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 应用构建Controller
 * 处理与ApplicationEntity相关的请求
 * 继承自BaseController类并实现IApplicationService接口
 * 
 * @version 1.0.0
 * @author luoxiaodong
 */
@Slf4j
@Api(tags = "GlobalConfig")
@RestController
@Scope("prototype")
@RequestMapping("/api/infra/smart/assistant/globalConfig")
public class GlobalConfigController extends BaseController<GlobalConfigEntity, IGlobalConfigService> {

    @Autowired
    private IGlobalConfigService service;

    /**
     * 获取ApplicationEntity的DataTables数据
     * 
     * @param request HttpServletRequest对象
     * @param model Model对象
     * @param page DatatablesPageBean对象
     * @return 包含DataTables数据的TableDataInfo对象
     */
    @ResponseBody
    @PostMapping("/datatables")
    public TableDataInfo datatables(HttpServletRequest request, Model model, DatatablesPageBean page) {
        log.debug("page = {}", ToStringBuilder.reflectionToString(page));
        return this.toPage(model, this.getFeign(), page);
    }

    /**
     * 保存全局配置信息
     * @param configDTO 全局配置 DTO 对象
     * @return 保存结果信息
     */
    @DataPermissionSave
    @PostMapping("/saveGlobalConfig")
    public AjaxResult saveGlobalConfig(@RequestBody @Validated GlobalConfigDTO configDTO) {
        GlobalConfigEntity entity = configDTO.toEntity();
        service.saveOrUpdate(entity);
        return AjaxResult.success("保存成功");
    }

    /**
     * 根据组织 ID 获取全局配置信息
     * @return 包含全局配置信息的 AjaxResult
     */
    @GetMapping("/getByOrg")
    public AjaxResult getByOrganizationId() {
        GlobalConfigEntity entity = service.getByOrganizationId(CurrentAccountJwt.get().getOrgId());
        if (entity == null) {
            return AjaxResult.success(GlobalConfigDTO.defaultDto());
        }
        GlobalConfigDTO dto = new GlobalConfigDTO();
        BeanUtils.copyProperties(entity, dto);
        return AjaxResult.success(dto);
    }

    @Override
    public IGlobalConfigService getFeign() {
        return this.service;
    }
}