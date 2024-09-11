package com.alinesno.infra.smart.assistant.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.brain.api.reponse.TaskContentDto;

import java.util.List;
import java.util.Map;

/**
 * 应用构建Service接口
 * 
 * @version 1.0.0
 * @author luoxiaodong
 */
public interface IIndustryRoleService extends IBaseService<IndustryRoleEntity> {

    /**
     * 保存角色工作流信息
     *
     * @param entity
     * @param roleId
     */
    void saveRoleChainInfo(String roleId);

    /**
     * 运行角色工作流
     * @param params
     * @param roleId
     */
    void runRoleChainByRoleId(Map<String , Object> params , long roleId);

    /**
     * 运行下一个角色业务
     * @param businessId
     * @param roleId
     */
    void runChainAgent(TaskContentDto businessId, long roleId);

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
}