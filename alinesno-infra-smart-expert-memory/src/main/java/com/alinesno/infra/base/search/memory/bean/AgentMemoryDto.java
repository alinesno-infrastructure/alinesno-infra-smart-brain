package com.alinesno.infra.base.search.memory.bean;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

/**
 * MemoryDto类，用于封装角色间记忆的数据传输对象。
 */
@ToString
@Data
public class AgentMemoryDto {

    @Min(1)
    private long chatScopeId ; // 会话范围ID

    @Min(1)
    private long agentId ; // 角色ID

    @NotBlank(message = "角色名称不能为空")
    private String agentName ; // 角色名称

    @Min(1)
    private long sourceRoleId ; // 当前角色ID（源角色）

    @NotBlank(message = "源角色名称不能为空")
    private String sourceRoleName ; // 当前角色名称

    @Min(1)
    private long targetRoleId ; // 目标角色ID（被记忆的角色）

    @NotBlank(message = "目标角色名称不能为空")
    private String targetRoleName ; // 目标角色名称

    @NotBlank(message = "记忆数据不能为空")
    private String data ; // 记忆数据

    private long timestamp ; // 记忆发生的时刻

}