package com.alinesno.infra.base.im.gateway.provider;

import com.alinesno.infra.base.im.service.IWorkflowService;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.SuperController;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.im.dto.MessageUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
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
@RestController
@Scope("prototype")
@RequestMapping(value = "/v1/api/infra/base/im/workflow")
public class WorkflowController extends SuperController {

    @Autowired
    private IWorkflowService workflowService ;

    /**
     * 根据业务id获取到消息内容
     */
    @GetMapping("/detail")
    public AjaxResult getWorkflowExecution(@RequestParam String businessId) {

        WorkflowExecutionDto dto = workflowService.getWorkflowExecution(businessId) ;

        return AjaxResult.success(dto) ;
    }

    /**
     * 更新消息内容
     */
    @PostMapping("/updateContent")
    public AjaxResult updateContent(@RequestBody MessageUpdateDto dto) {

        log.debug("updateContent:{}", dto);

        // 更新消息内容
        workflowService.updateContent(dto)  ;

        return ok() ;
    }

}