package com.alinesno.infra.smart.assistant.adapter.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 重新排序输出数据传输对象
 * 用于封装重新排序操作的输出，包括结果列表、使用情况和请求ID
 */
@ToString
@NoArgsConstructor
@Data
public class RerankOutput {

    // 结果列表，包含重新排序后的文档列表
    private Output output;

    // 使用情况，包含总token数
    private Usage usage;

    // 请求ID，用于标识和追踪请求
    private String request_id;

}
