package com.alinesno.infra.smart.assistant.service;

import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.api.*;
import com.alinesno.infra.smart.assistant.api.config.RoleDeepSearchConfigDto;
import com.alinesno.infra.smart.assistant.api.config.RoleFlowConfigDto;
import com.alinesno.infra.smart.assistant.api.config.RoleReActConfigDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.brain.api.dto.PromptMessageDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 应用构建Service接口
 * 
 * @version 1.0.0
 * @author luoxiaodong
 */
public interface IIndustryRoleService extends IBaseService<IndustryRoleEntity> {

    /**
     * 获取最新的角色
      * @return
     */
    List<IndustryRoleEntity> getNewestRole(PermissionQuery query);

    /**
     * 通过用户名获取角色
     * @param users
     * @return
     */
    List<IndustryRoleEntity> getRoleByUserName(List<String> users);

    /**
     * 获取到帮助助手Agent角色(平台有一个默认的帮助角色)
     * @return
     */
    IndustryRoleEntity getDefaultHelpAgent();

    /**
     * 更新角色提示信息
     * @param messageDto
     * @param roleId
     */
    void updatePromptContent(List<PromptMessageDto> messageDto, String roleId);

    /**
     * 运行执行RoleAgent角色
     *
     * @param taskInfo
     * @return
     */
    CompletableFuture<WorkflowExecutionDto> runRoleAgent(MessageTaskInfo taskInfo);

    /**
     * 批量生成角色
     * @param allEntities
     */
    void batchCreateRole(List<IndustryRoleEntity> allEntities);

    /**
     * 更新角色脚本信息
     *
     * @param dto 角色脚本数据传输对象，包含需要更新的角色脚本信息
     */
    void updateRoleScript(RoleScriptDto dto);

    /**
     * 验证角色脚本的合法性
     *
     * @param dto 角色脚本数据传输对象，包含需要验证的角色脚本信息
     * @return
     */
    CompletableFuture<WorkflowExecutionDto> validateRoleScript(RoleScriptDto dto);

    /**
     * 创建角色
     *
     * @param e
     */
    void createRole(IndustryRoleEntity e);

    /**
     * 推荐组织Hero角色
     * @param roleId
     * @param orgId
     */
    void recommended(long roleId , long orgId);

    /**
     * 获取推荐角色
     * @param orgId
     * @return
     */
    IndustryRoleEntity getRecommendRole(long orgId);

    /**
     * 保存角色和工具关联
     * @param dto
     */
    void saveRoleWithTool(RoleToolRequestDTO dto);

    /**
     * 验证ReAct角色
     * @param dto
     * @return
     */
    CompletableFuture<WorkflowExecutionDto> validateReActRole(ReActRoleScriptDto dto);

    /**
     * 启用角色和员工
     *
     * @param roleId
     * @param isPush 是否为组织单独推送
     */
    void employRole(long roleId, long orgId , long userId , long deptId, boolean isPush);

    /**
     * 更新ReAct角色配置信息
     * @param dto
     */
    void saveRoleWithReActConfig(RoleReActConfigDto dto);

    /**
     * 修改角色基础信息
     * @param dto
     */
    void modifyInfo(RoleInfoDto dto);

    /**
     * 更新角色流程配置信息
     * @param flowConfigDto
     * @param roleId
     */
    void updateFlowConfig(RoleFlowConfigDto flowConfigDto, Long roleId);

    /**
     * 获取角色当前频道问题建议
     * @param dto
     * @return
     */
    List<String> getQuestionSuggestion(RoleQuestionSuggestionDto dto);

    /**
     * 保存角色和工作流配置关联
     * @param dto
     */
    void saveRoleWithWorkflowConfig(RoleFlowConfigDto dto);

    /**
     * 获取公开角色列表
     * @param query
     * @return
     */
    List<IndustryRoleEntity> listPublicRole(PermissionQuery query);

    /**
     * 获取所有组织角色和公共角色列表
     * @param query
     * @return
     */
    List<IndustryRoleEntity> listOrgRoleAndPublicRole(PermissionQuery query);

    /**
     * 保存角色和深度搜索配置关联
     * @param dto
     */
    void saveRoleWithDeepSearchConfig(RoleDeepSearchConfigDto dto);
}