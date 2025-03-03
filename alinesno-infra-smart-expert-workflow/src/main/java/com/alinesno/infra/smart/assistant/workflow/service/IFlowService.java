package com.alinesno.infra.smart.assistant.workflow.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.workflow.FlowExpertService;
import com.alinesno.infra.smart.assistant.workflow.dto.FlowDto;
import com.alinesno.infra.smart.assistant.workflow.dto.WorkflowRequestDto;
import com.alinesno.infra.smart.assistant.workflow.entity.FlowEntity;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.entity.MessageEntity;

/**
 * 工作流服务接口，负责处理工作流基础信息和元数据信息相关的业务操作。
 * 继承自 IBaseService 接口，借助其提供的通用方法，可对工作流数据进行基本的增删改查等操作。
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
public interface IFlowService extends IBaseService<FlowEntity> {

    /**
     * 保存角色工作流信息
     * @param roleId
     * @param flowDto
     */
    void saveRoleFlow(Long roleId, WorkflowRequestDto flowDto);

    /**
     * 发布工作流
     * @param roleId
     */
    void publishFlow(Long roleId);

    /**
     * 获取指定角色最新版本的已发布流程
     * @param roleId 角色ID
     * @return 最新版本的已发布流程实体，如果不存在则返回 null
     */
    FlowEntity getLatestPublishedFlowByRoleId(Long roleId);

    /**
     * 获取指定角色的未发布流程
     * @param roleId 角色ID
     * @return 未发布流程实体，如果不存在则返回 null
     */
    FlowEntity getUnpublishedFlowByRoleId(Long roleId);

    /**
     * 获取指定角色最新版本的已发布流程
     * @param roleId 角色ID
     * @return 最新版本的已发布流程实体，如果不存在则返回 null
     */
    FlowDto getLatestFlowByRoleId(Long roleId);

    /**
     * 运行Agent角色
     *
     * @param taskInfo
     * @param role
     * @param workflowExecution
     * @param flowExpertService
     */
    String runRoleFlow(MessageTaskInfo taskInfo, IndustryRoleEntity role, MessageEntity workflowExecution, FlowExpertService flowExpertService);

}