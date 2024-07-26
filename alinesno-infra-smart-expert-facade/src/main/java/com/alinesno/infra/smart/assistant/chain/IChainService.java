package com.alinesno.infra.smart.assistant.chain;

import com.alinesno.infra.smart.assistant.entity.RoleChainEntity;
import com.alinesno.infra.smart.assistant.entity.RoleChainScriptEntity;

/**
 * 业务服务
 */
public interface IChainService {

    /**
     * 刷新规范热数据
     */
    void reloadRule();

    /**
     * 验证保存的脚本是否正确
     * @param entity
     */
    void checkScript(RoleChainScriptEntity entity);

    /**
     * 运行脚本
     * @param roleChain
     */
    void executeRule(RoleChainEntity roleChain);
}
