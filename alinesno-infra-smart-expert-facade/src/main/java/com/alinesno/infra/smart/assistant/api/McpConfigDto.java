package com.alinesno.infra.smart.assistant.api;

import com.alinesno.infra.common.facade.base.BaseDto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * McpConfigDto类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class McpConfigDto extends BaseDto {

    @NotBlank(message = "MCP名称不能为空")
    private String mcpName;

    @NotBlank(message = "MCP地址不能为空")
    private String mcpUrl;

    @NotBlank(message = "MCP密钥不能为空")
    private String mcpToken;

}
