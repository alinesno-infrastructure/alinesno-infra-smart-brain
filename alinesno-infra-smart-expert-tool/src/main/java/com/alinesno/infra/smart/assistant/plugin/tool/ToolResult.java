package com.alinesno.infra.smart.assistant.plugin.tool;

import lombok.Data;

@Data
public class ToolResult {

    private Object output ;  // 输出结果
    private boolean isFinished ; // 是否结束本轮会话

}
