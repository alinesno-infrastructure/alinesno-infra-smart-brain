package com.alinesno.infra.smart.assistant.plugin.controller;

import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.login.account.CurrentAccountJwt;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.entity.ToolTypeEntity;
import com.alinesno.infra.smart.assistant.service.IToolTypeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
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
@Api(tags = "Channel")
@RestController
@Scope("prototype")
@RequestMapping("/api/infra/smart/assistant/toolType")
public class ToolTypeController extends BaseController<ToolTypeEntity, IToolTypeService> {

    @Autowired
    private IToolTypeService service;

    /**
     * 获取ApplicationEntity的DataTables数据
     * 
     * @param request HttpServletRequest对象
     * @param model Model对象
     * @param page DatatablesPageBean对象
     * @return 包含DataTables数据的TableDataInfo对象
     */
    @DataPermissionScope
    @ResponseBody
    @PostMapping("/datatables")
    public TableDataInfo datatables(HttpServletRequest request, Model model, DatatablesPageBean page) {
        log.debug("page = {}", ToStringBuilder.reflectionToString(page));

        // 如果用户所在组织工具类型为空，则默认初始化工具
        service.initToolType(CurrentAccountJwt.get().getOrgId());

        return this.toPage(model, this.getFeign(), page);
    }

    @DataPermissionSave
    @Override
    public AjaxResult save(Model model, @RequestBody ToolTypeEntity entity) throws Exception {
        return super.save(model, entity);
    }

    /**
     * 获取到组织的所有工具分类
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/getAllToolType")
    public AjaxResult getAllToolType(PermissionQuery query) {
        LambdaQueryWrapper<ToolTypeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.setEntityClass(ToolTypeEntity.class);
        query.toWrapper(queryWrapper);
        return AjaxResult.success(service.list(queryWrapper)) ;
    }

    @Override
    public IToolTypeService getFeign() {
        return this.service;
    }
}