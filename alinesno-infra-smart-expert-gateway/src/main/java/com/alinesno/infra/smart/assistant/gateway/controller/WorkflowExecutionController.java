package com.alinesno.infra.smart.assistant.gateway.controller;

import com.alinesno.infra.common.facade.pageable.ConditionDto;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.WorkflowExecutionEntity;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.assistant.service.IWorkflowExecutionService;
import io.swagger.annotations.Api;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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
@RequestMapping("/api/infra/smart/assistant/workflowExecution")
public class WorkflowExecutionController extends BaseController<WorkflowExecutionEntity, IWorkflowExecutionService> {

    @Autowired
    private IWorkflowExecutionService service;

    @Autowired
    private IIndustryRoleService roleService;

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

        String roleId = request.getParameter("roleId") ;
        if(StringUtils.isNotBlank(roleId)){

            IndustryRoleEntity role = roleService.getById(roleId) ;
            Long workflowId = role.getChainId() ;
            List<ConditionDto> conditionDtos = new ArrayList<>() ;

            ConditionDto conditionDto = new ConditionDto() ;
            conditionDto.setValue(workflowId+"");
            conditionDto.setColumn("workflow_id");

            conditionDtos.add(conditionDto) ;

            page.setConditionList(conditionDtos);
        }

        return this.toPage(model, this.getFeign(), page);
    }

    @Override
    public IWorkflowExecutionService getFeign() {
        return this.service;
    }
}