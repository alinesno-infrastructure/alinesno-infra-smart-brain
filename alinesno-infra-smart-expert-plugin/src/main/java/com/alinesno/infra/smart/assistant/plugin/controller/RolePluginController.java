package com.alinesno.infra.smart.assistant.plugin.controller;

import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.entity.RolePluginEntity;
import com.alinesno.infra.smart.assistant.plugin.service.IRolePluginService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 角色插件管理
 */
@Slf4j
@RequestMapping("/api/assistant/role/plugin")
@RestController
public class RolePluginController extends BaseController<RolePluginEntity , IRolePluginService> {

    @Autowired
    private IRolePluginService service;

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

    @Override
    public IRolePluginService getFeign() {
        return this.service;
    }

    /**
     * 重新加载插件
     * @return
     */
    @GetMapping("/reloadPlugin")
    public AjaxResult reloadPlugin(){
        service.reloadPlugin() ;
        return AjaxResult.success();
    }

    /**
     * 安装插件
     * @return
     */
    @GetMapping("/installPlugin/{pluginId}")
    public AjaxResult installPlugin(@PathVariable("pluginId") long pluginId){

        log.debug("pluginId = {}" , pluginId);

        long accountId = 1L ;
        service.installPlugin(accountId , pluginId) ;

        return AjaxResult.success();
    }

    /**
     * 删除插件
     * @return
     */
    @GetMapping("/removePlugin")
    public AjaxResult removePlugin(){

        return AjaxResult.success();
    }

    /**
     * 更新插件
     * @return
     */
    @GetMapping("/updatePlugin/{pluginId}")
    public AjaxResult updatePlugin(@PathVariable("pluginId") long pluginId){

        long accountId = 1L ;
        service.installPlugin(accountId , pluginId) ;

        return AjaxResult.success();
    }

}

