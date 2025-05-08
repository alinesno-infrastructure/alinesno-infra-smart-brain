package com.alinesno.infra.smart.assistant.adapter.dto;

import lombok.Data;

/**
 * MCP任务执行结果
 */
@Data
public class McpToolResultDto {

    private Object output ;  // 输出结果
    private boolean isFinished ; // 是否结束本轮会话

}
