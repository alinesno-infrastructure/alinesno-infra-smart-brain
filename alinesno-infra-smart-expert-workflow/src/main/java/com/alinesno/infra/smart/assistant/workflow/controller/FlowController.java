package com.alinesno.infra.smart.assistant.workflow.controller;

import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.smart.assistant.workflow.dto.FlowDto;
import com.alinesno.infra.smart.assistant.workflow.dto.WorkflowRequestDto;
import com.alinesno.infra.smart.assistant.workflow.entity.FlowEntity;
import com.alinesno.infra.smart.assistant.workflow.service.IFlowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;

/**
 * 流程控制
 * @version 1.0.0
 * @author luoxiaodong
 */
@Slf4j
@RestController
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequestMapping("/api/infra/smart/assistant/flow")
public class FlowController {

    @Autowired
    private IFlowService flowService;

    @PostMapping("/processAndSave")
    public AjaxResult processAndSave(@RequestBody WorkflowRequestDto flowDto, @RequestParam Long roleId) {

        flowService.saveRoleFlow(roleId, flowDto);  // 保存工作流

        return AjaxResult.success();
    }

    /**
     * 发布工作流接口
     * @param roleId 角色ID
     * @return 操作结果
     */
    @PostMapping("/publish")
    public AjaxResult publishFlow(@RequestParam Long roleId) {
        try {
            flowService.publishFlow(roleId);
            return AjaxResult.success("工作流发布成功");
        } catch (Exception e) {
            log.error("工作流发布失败", e);
            return AjaxResult.error("工作流发布失败：" + e.getMessage());
        }
    }

    /**
     * 获取指定角色最新版本的已发布流程接口
     * @param roleId 角色ID
     * @return 包含最新版本已发布流程的结果
     */
    @GetMapping("/latest")
    public AjaxResult getLatestFlowByRoleId(@RequestParam Long roleId) {
        FlowDto flowDto = flowService.getLatestFlowByRoleId(roleId);
        return AjaxResult.success(flowDto);
    }

    /**
     * 获取指定角色最新版本的已发布流程接口
     * @param roleId 角色ID
     * @return 包含最新版本已发布流程的结果
     */
    @GetMapping("/latestPublished")
    public AjaxResult getLatestPublishedFlowByRoleId(@RequestParam Long roleId) {
        FlowEntity flowEntity = flowService.getLatestPublishedFlowByRoleId(roleId);
        if (flowEntity != null) {
            return AjaxResult.success(flowEntity);
        }
        return AjaxResult.error("未找到指定角色的最新已发布流程");
    }

    /**
     * 获取指定角色的未发布流程接口
     * @param roleId 角色ID
     * @return 包含未发布流程的结果
     */
    @GetMapping("/unpublished")
    public AjaxResult getUnpublishedFlowByRoleId(@RequestParam Long roleId) {
        FlowEntity flowEntity = flowService.getUnpublishedFlowByRoleId(roleId);
        if (flowEntity != null) {
            return AjaxResult.success(flowEntity);
        }
        return AjaxResult.error("未找到指定角色的未发布流程");
    }
}