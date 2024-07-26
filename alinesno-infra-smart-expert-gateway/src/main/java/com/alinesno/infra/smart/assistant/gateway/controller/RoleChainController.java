package com.alinesno.infra.smart.assistant.gateway.controller;

import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.entity.RoleChainEntity;
import com.alinesno.infra.smart.assistant.service.IRoleChainService;
import io.swagger.annotations.Api;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
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
@Api(tags = "ApiKey")
@RestController
@Scope("prototype")
@RequestMapping("/api/infra/smart/assistant/roleChain")
public class RoleChainController extends BaseController<RoleChainEntity, IRoleChainService> {

    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    private IRoleChainService service;

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
     * 更新配置
     * @param model
     * @param entity
     * @return
     * @throws Exception
     */
    @Override
    public AjaxResult update(Model model, @RequestBody RoleChainEntity entity) throws Exception {

        service.updateRoleChain(entity);

        return this.ok();
    }

    /**
     * 新增加配置
     * @param model
     * @param entity
     * @return
     * @throws Exception
     */
    @Override
    public AjaxResult save(Model model, @RequestBody RoleChainEntity entity) throws Exception {

        entity.setEnable("1");
        entity.setChainApplicationName(applicationName);

        service.saveRoleChain(entity);

        return AjaxResult.success();
    }

    @Override
    public IRoleChainService getFeign() {
        return this.service;
    }
}