package com.alinesno.infra.smart.assistant.screen.controller;

import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.screen.entity.RoleExecuteEntity;
import com.alinesno.infra.smart.assistant.screen.event.TaskAssignedEvent;
import com.alinesno.infra.smart.assistant.screen.service.IRoleExecuteService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 处理与BusinessLogEntity相关的请求的Controller。
 * 继承自BaseController类并实现IBusinessLogService接口。
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@Slf4j
@RestController
@Scope(SpringInstanceScope.PROTOTYPE)
@RequestMapping("/api/infra/smart/assistant/screenLeader")
public class LeaderModelController extends BaseController<RoleExecuteEntity, IRoleExecuteService> {

    @Autowired
    private IRoleExecuteService service;

    @Value("${alinesno.file.local.path:${java.io.tmpdir}}")
    private String localPath;

    @Autowired
    private ApplicationEventPublisher publisher;

    /**
     * 获取BusinessLogEntity的DataTables数据。
     *
     * @param request HttpServletRequest对象。
     * @param model   Model对象。
     * @param page    DatatablesPageBean对象。
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
     * 生成计划
     * @return
     */
    @PostMapping("/plan")
    public AjaxResult plan() {

        // 发送Event通知给Worker
        // 列出并分配任务
        String[] tasks = {"Task1", "Task2", "Task3"};
        for (String task : tasks) {
            publisher.publishEvent(new TaskAssignedEvent(this, task));
        }

        return AjaxResult.success();
    }





    @Override
    public IRoleExecuteService getFeign() {
        return this.service;
    }
}