package com.alinesno.infra.smart.assistant.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.brain.api.dto.PromptMessageDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;

import java.util.List;

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
    List<IndustryRoleEntity> getNewestRole();

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
    WorkflowExecutionDto runRoleAgent(MessageTaskInfo taskInfo);
}