package com.alinesno.infra.smart.assistant.gateway.controller;

import com.alinesno.infra.common.facade.pageable.ConditionDto;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.entity.WorkflowNodeExecutionEntity;
import com.alinesno.infra.smart.assistant.service.IWorkflowNodeExecutionService;
import io.swagger.annotations.Api;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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
@RequestMapping("/api/infra/smart/assistant/workflowNodeExecution")
public class WorkflowNodeExecutionController extends BaseController<WorkflowNodeExecutionEntity, IWorkflowNodeExecutionService> {

    @Autowired
    private IWorkflowNodeExecutionService service;

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

        List<ConditionDto> conditionDtos = new ArrayList<>() ;

        ConditionDto conditionDto = new ConditionDto() ;
        conditionDto.setValue(request.getParameter("currentWorkflowId"));
        conditionDto.setColumn("workflow_execution_id");

        conditionDtos.add(conditionDto) ;

        page.setConditionList(conditionDtos);

        return this.toPage(model, this.getFeign(), page);
    }

    @Override
    public IWorkflowNodeExecutionService getFeign() {
        return this.service;
    }
}











