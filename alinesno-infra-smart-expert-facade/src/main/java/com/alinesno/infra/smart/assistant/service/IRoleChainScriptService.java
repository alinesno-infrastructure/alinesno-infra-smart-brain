package com.alinesno.infra.smart.assistant.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.entity.RoleChainScriptEntity;

/**
 * 应用构建Service接口
 * 
 * @version 1.0.0
 * @author luoxiaodong
 */
public interface IRoleChainScriptService extends IBaseService<RoleChainScriptEntity> {

    /**
     * 添加数据库脚本并刷新
     * @param entity
     */
    void saveScript(RoleChainScriptEntity entity);

    /**
     * 删除脚本数据
     * @param ids
     */
    void deleteScript(String ids);

    /**
     * 更新脚本数据
     * @param entity
     */
    void updateScript(RoleChainScriptEntity entity);
}