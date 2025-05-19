package com.alinesno.infra.smart.assistant.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.entity.McpConfigEntity;

/**
 * MCP配置服务接口
 */
public interface IMcpConfigService extends IBaseService<McpConfigEntity> {

    /**
     * 根据组织ID获取MCP配置
     * @param orgId 组织ID
     * @return MCP配置实体
     */
    McpConfigEntity getByOrgId(Long orgId);

    /**
     * 验证MCP地址有效性
     * @param mcpUrl MCP地址
     * @return 是否有效
     */
    boolean validateMcpUrl(String mcpUrl);
}