package com.alinesno.infra.smart.assistant.gateway.provider;

import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.SuperController;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

/**
 * 这是一个助手控制器类
 * 该类用于提供助手功能的接口
 * 作者：luoxiaodong
 * 版本：1.0.0
 */
@Slf4j
@RestController
@Scope("prototype")
@RequestMapping("/api/infra/smart/assistant/agent")
public class AssistantController extends SuperController {

    @Autowired
    private IIndustryRoleService roleService ;

    /**
     * 运行角色智能体
     * @return
     */
    @PostMapping("/runRoleAgent")
    public AjaxResult runRoleAgent(@RequestBody MessageTaskInfo taskInfo) {
        CompletableFuture<WorkflowExecutionDto> dto = roleService.runRoleAgent(taskInfo) ;
        return AjaxResult.success(dto) ;
    }

}
