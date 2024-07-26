package com.alinesno.infra.smart.assistant.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.entity.RoleChainEntity;

/**
 * 应用构建Service接口
 * 
 * @version 1.0.0
 * @author luoxiaodong
 */
public interface IRoleChainService extends IBaseService<RoleChainEntity> {

    /**
     * 运行工作流
     * @param chainId
     */
    void runById(Long chainId);

    /**
     * 保存流程节点
     * @param entity
     */
    void saveRoleChain(RoleChainEntity entity);

    /**
     * 更新流程节点
     * @param entity
     */
    void updateRoleChain(RoleChainEntity entity);
}