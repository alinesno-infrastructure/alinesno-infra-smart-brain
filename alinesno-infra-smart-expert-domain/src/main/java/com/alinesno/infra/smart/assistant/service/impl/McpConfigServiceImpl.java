package com.alinesno.infra.smart.assistant.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.enums.HasStatusEnums;
import com.alinesno.infra.smart.assistant.entity.McpConfigEntity;
import com.alinesno.infra.smart.assistant.mapper.McpConfigMapper;
import com.alinesno.infra.smart.assistant.service.IMcpConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * MCP配置服务实现
 */
@Slf4j
@Service
public class McpConfigServiceImpl extends IBaseServiceImpl<McpConfigEntity, McpConfigMapper> implements IMcpConfigService {

    @Override
    public McpConfigEntity getByOrgId(Long orgId) {
        LambdaQueryWrapper<McpConfigEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(McpConfigEntity::getHasStatus , HasStatusEnums.LEGAL.value);
        wrapper.eq(McpConfigEntity::getOrgId, orgId);
        return getOne(wrapper) ;
    }

    @Override
    public boolean validateMcpUrl(String mcpUrl) {
        try {
            URL url = new URL(mcpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            return (responseCode == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            log.error("验证MCP地址失败: {}", e.getMessage());
            return false;
        }
    }
}